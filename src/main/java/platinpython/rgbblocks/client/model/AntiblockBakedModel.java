package platinpython.rgbblocks.client.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jspecify.annotations.Nullable;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.block.entity.RGBBlockEntity;
import platinpython.rgbblocks.util.registries.BlockRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AntiblockBakedModel implements BakedModel {
    private final BakedModel base;
    private final Map<Direction, BakedQuad> bgQuads;
    private final Map<Direction, BakedQuad> frameQuads;
    private final LoadingCache<Direction, List<BakedQuad>> bgCache =
        CacheBuilder.newBuilder().build(new CacheLoader<>() {
            @Override
            public List<BakedQuad> load(Direction key) {
                return genBackgroundQuads(bgQuads.get(key), key);
            }
        });
    private final LoadingCache<Connections, List<BakedQuad>> quadCache =
        CacheBuilder.newBuilder().build(new CacheLoader<>() {
            @Override
            public List<BakedQuad> load(Connections key) {
                if (key.isClosed()) {
                    return List.of(bgQuads.get(key.side), frameQuads.get(key.side));
                }
                return genCtmQuads(frameQuads.get(key.side), bgCache.getUnchecked(key.side), key);
            }
        });

    public AntiblockBakedModel(
        BakedModel baseModel,
        Map<Direction, BakedQuad> bgQuads,
        Map<Direction, BakedQuad> frameQuads
    ) {
        this.base = baseModel;
        this.bgQuads = bgQuads;
        this.frameQuads = frameQuads;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
        return this.getQuads(state, side, random, ModelData.EMPTY, null);
    }

    @Override
    public List<BakedQuad> getQuads(
        @Nullable BlockState state,
        @Nullable Direction side,
        RandomSource random,
        ModelData extraData,
        @Nullable RenderType renderType
    ) {
        if (side == null) {
            return Collections.emptyList();
        }

        List<BakedQuad> quads = new ArrayList<>();

        AntiblockModelData modelData = extraData.get(AntiblockModelData.ANTIBLOCK_MODEL_DATA);
        if (modelData == null) {
            quads.add(bgQuads.get(side));
            quads.add(frameQuads.get(side));
            return quads;
        }

        quads.addAll(quadCache.getUnchecked(modelData.connections.get(side)));

        return quads;
    }

    private static List<BakedQuad> genBackgroundQuads(BakedQuad quad, Direction side) {
        List<BakedQuad> quads = new ArrayList<>();

        quads.add(genBackgroundQuad(quad, false, false, quad.getSprite(), side));
        quads.add(genBackgroundQuad(quad, true, false, quad.getSprite(), side));
        quads.add(genBackgroundQuad(quad, false, true, quad.getSprite(), side));
        quads.add(genBackgroundQuad(quad, true, true, quad.getSprite(), side));

        return quads;
    }

    private static BakedQuad genBackgroundQuad(
        BakedQuad quad,
        boolean down,
        boolean right,
        TextureAtlasSprite sprite,
        Direction side
    ) {
        int[] vertexData = cloneVertexData(quad);

        float minX = right ? .5F : 0F;
        float minY = down ? 0F : .5F;
        if (side == Direction.UP) {
            minY = (minY + .5F) % 1F;
        }
        cutQuad(vertexData, minX, minY, minX + .5F, minY + .5F, side);

        float u = right ? 0.5F : 0;
        float v = down ? 0.5F : 0;
        applyUVs(vertexData, true, sprite, u, v, side);

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
    }

    private static List<BakedQuad> genCtmQuads(BakedQuad frameQuad, List<BakedQuad> bgQuads, Connections connections) {
        TextureAtlasSprite sprite = frameQuad.getSprite();
        TextureAtlasSprite ctmSprite = Minecraft.getInstance()
            .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
            .apply(new ResourceLocation(RGBBlocks.MOD_ID, "block/antiblock_ctm"));

        List<BakedQuad> quads = new ArrayList<>(bgQuads);

        Direction side = connections.side;

        quads.add(
            genCtmQuad(
                frameQuad, connections.left, connections.up, connections.upLeft, false, false, 0F, .5F, sprite,
                ctmSprite, side
            )
        );
        quads.add(
            genCtmQuad(
                frameQuad, connections.left, connections.down, connections.downLeft, true, false, 0F, 0F, sprite,
                ctmSprite, side
            )
        );
        quads.add(
            genCtmQuad(
                frameQuad, connections.right, connections.up, connections.upRight, false, true, .5F, .5F, sprite,
                ctmSprite, side
            )
        );
        quads.add(
            genCtmQuad(
                frameQuad, connections.right, connections.down, connections.downRight, true, true, .5F, 0F, sprite,
                ctmSprite, side
            )
        );

        return quads;
    }

    private static BakedQuad genCtmQuad(
        BakedQuad quad,
        boolean x,
        boolean y,
        boolean xy,
        boolean down,
        boolean right,
        float minX,
        float minY,
        TextureAtlasSprite defSprite,
        TextureAtlasSprite ctmSprite,
        Direction side
    ) {
        int[] vertexData = cloneVertexData(quad);
        if (side == Direction.UP) {
            minY = (minY + .5F) % 1F;
        }
        cutQuad(vertexData, minX, minY, minX + .5F, minY + .5F, side);

        boolean closed = !x && !y;
        TextureAtlasSprite sprite = closed ? defSprite : ctmSprite;
        float u = getU(x, y, xy) + (right ? (closed ? 0.5F : 0.25F) : 0);
        float v = getV(x, y, xy) + (down ? (closed ? 0.5F : 0.25F) : 0);
        applyUVs(vertexData, closed, sprite, u, v, side);

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
    }

    private static int[] cloneVertexData(BakedQuad quad) {
        int size = quad.getVertices().length;
        int[] vertexData = new int[size];
        System.arraycopy(quad.getVertices(), 0, vertexData, 0, size);
        return vertexData;
    }

    private static float getU(boolean x, boolean y, boolean xy) {
        if (!x && !y) {
            return 0;
        }

        if (x && !y) {
            return 0;
        } else if (!x && y) {
            return 0.5F;
        } else {
            return xy ? 0 : 0.5F;
        }
    }

    private static float getV(boolean x, boolean y, boolean xy) {
        if (!x && !y) {
            return 0;
        }

        if (x && !y) {
            return 0.5F;
        } else if (!x && y) {
            return 0;
        } else {
            return xy ? 0 : 0.5F;
        }
    }

    private static void cutQuad(int[] vertexData, float minX, float minY, float maxX, float maxY, Direction side) {
        float[][] pos = new float[4][3];
        for (int i = 0; i < 4; i++) {
            unpackPosition(vertexData, pos[i], i);
        }

        if (side.getAxis() != Direction.Axis.Y) {
            pos[0][1] = maxY;
            pos[1][1] = minY;
            pos[2][1] = minY;
            pos[3][1] = maxY;

            boolean xAxis = side.getAxis() == Direction.Axis.X;
            boolean positive = side.getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            pos[0][xAxis ? 2 : 0] = positive ? minX : (1F - minX);
            pos[1][xAxis ? 2 : 0] = positive ? minX : (1F - minX);
            pos[2][xAxis ? 2 : 0] = positive ? maxX : (1F - maxX);
            pos[3][xAxis ? 2 : 0] = positive ? maxX : (1F - maxX);
        } else {
            pos[0][0] = maxX;
            pos[1][0] = maxX;
            pos[2][0] = minX;
            pos[3][0] = minX;

            boolean up = side == Direction.UP;
            pos[0][2] = up ? maxY : minY;
            pos[1][2] = up ? minY : maxY;
            pos[2][2] = up ? minY : maxY;
            pos[3][2] = up ? maxY : minY;
        }

        for (int i = 0; i < 4; i++) {
            packPosition(pos[i], vertexData, i);
        }
    }

    private static void applyUVs(
        int[] vertexData,
        boolean closed,
        TextureAtlasSprite sprite,
        float u0,
        float v0,
        Direction side
    ) {
        float uMin = sprite.getU(u0);
        float vMin = sprite.getV(v0);
        float uMax = sprite.getU(u0 + (closed ? 0.5F : 0.25F));
        float vMax = sprite.getV(v0 + (closed ? 0.5F : 0.25F));

        float[][] uv = new float[4][2];
        for (int i = 0; i < 4; i++) {
            unpackUV(vertexData, uv[i], i);
        }

        boolean yAxis = side.getAxis() == Direction.Axis.Y;

        uv[0][0] = yAxis ? uMax : uMin;
        uv[1][0] = yAxis ? uMax : uMin;
        uv[2][0] = yAxis ? uMin : uMax;
        uv[3][0] = yAxis ? uMin : uMax;

        uv[0][1] = yAxis ? vMax : vMin;
        uv[1][1] = yAxis ? vMin : vMax;
        uv[2][1] = yAxis ? vMin : vMax;
        uv[3][1] = yAxis ? vMax : vMin;

        for (int i = 0; i < 4; i++) {
            packUV(uv[i], vertexData, i);
        }
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        if (!(getAntiblockAt(level, pos) instanceof RGBBlockEntity blockEntity)) {
            return modelData;
        }

        boolean north = false;
        boolean east = false;
        boolean south = false;
        boolean west = false;
        boolean up = false;
        boolean down = false;
        boolean upNorth = false;
        boolean upNorthColor = false;
        boolean upEast = false;
        boolean upEastColor = false;
        boolean upSouth = false;
        boolean upSouthColor = false;
        boolean upWest = false;
        boolean upWestColor = false;
        boolean northEast = false;
        boolean northEastColor = false;
        boolean southEast = false;
        boolean southEastColor = false;
        boolean southWest = false;
        boolean southWestColor = false;
        boolean northWest = false;
        boolean northWestColor = false;
        boolean downNorth = false;
        boolean downNorthColor = false;
        boolean downEast = false;
        boolean downEastColor = false;
        boolean downSouth = false;
        boolean downSouthColor = false;
        boolean downWest = false;
        boolean downWestColor = false;

        if (getAntiblockAt(level, pos.north()) instanceof RGBBlockEntity otherBlockEntity) {
            north = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.east()) instanceof RGBBlockEntity otherBlockEntity) {
            east = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.south()) instanceof RGBBlockEntity otherBlockEntity) {
            south = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.west()) instanceof RGBBlockEntity otherBlockEntity) {
            west = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.above()) instanceof RGBBlockEntity otherBlockEntity) {
            up = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.below()) instanceof RGBBlockEntity otherBlockEntity) {
            down = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.above().north()) instanceof RGBBlockEntity otherBlockEntity) {
            upNorth = true;
            upNorthColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.above().east()) instanceof RGBBlockEntity otherBlockEntity) {
            upEast = true;
            upEastColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.above().south()) instanceof RGBBlockEntity otherBlockEntity) {
            upSouth = true;
            upSouthColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.above().west()) instanceof RGBBlockEntity otherBlockEntity) {
            upWest = true;
            upWestColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.north().east()) instanceof RGBBlockEntity otherBlockEntity) {
            northEast = true;
            northEastColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.south().east()) instanceof RGBBlockEntity otherBlockEntity) {
            southEast = true;
            southEastColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.south().west()) instanceof RGBBlockEntity otherBlockEntity) {
            southWest = true;
            southWestColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.north().west()) instanceof RGBBlockEntity otherBlockEntity) {
            northWest = true;
            northWestColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.below().north()) instanceof RGBBlockEntity otherBlockEntity) {
            downNorth = true;
            downNorthColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.below().east()) instanceof RGBBlockEntity otherBlockEntity) {
            downEast = true;
            downEastColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.below().south()) instanceof RGBBlockEntity otherBlockEntity) {
            downSouth = true;
            downSouthColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        if (getAntiblockAt(level, pos.below().west()) instanceof RGBBlockEntity otherBlockEntity) {
            downWest = true;
            downWestColor = otherBlockEntity.getColor() == blockEntity.getColor();
        }
        boolean upNorthEast = getAntiblockAt(level, pos.above().north().east()) instanceof RGBBlockEntity;
        boolean upSouthEast = getAntiblockAt(level, pos.above().south().east()) instanceof RGBBlockEntity;
        boolean upSouthWest = getAntiblockAt(level, pos.above().south().west()) instanceof RGBBlockEntity;
        boolean upNorthWest = getAntiblockAt(level, pos.above().north().west()) instanceof RGBBlockEntity;
        boolean downNorthEast = getAntiblockAt(level, pos.below().north().east()) instanceof RGBBlockEntity;
        boolean downSouthEast = getAntiblockAt(level, pos.below().south().east()) instanceof RGBBlockEntity;
        boolean downSouthWest = getAntiblockAt(level, pos.below().south().west()) instanceof RGBBlockEntity;
        boolean downNorthWest = getAntiblockAt(level, pos.below().north().west()) instanceof RGBBlockEntity;

        return modelData.derive()
            .with(
                AntiblockModelData.ANTIBLOCK_MODEL_DATA,
                new AntiblockModelData(
                    new Connections(
                        Direction.NORTH, up && !upNorth, down && !downNorth, east && !northEast, west && !northWest,
                        upEast && upEastColor && !upNorthEast, upWest && upWestColor && !upNorthWest,
                        downEast && downEastColor && !downNorthEast, downWest && downWestColor && !downNorthWest
                    ),
                    new Connections(
                        Direction.EAST, up && !upEast, down && !downEast, south && !southEast, north && !northEast,
                        upSouth && upSouthColor && !upSouthEast, upNorth && upNorthColor && !upNorthEast,
                        downSouth && downSouthColor && !downSouthEast, downNorth && downNorthColor && !downNorthEast
                    ),
                    new Connections(
                        Direction.SOUTH, up && !upSouth, down && !downSouth, west && !southWest, east && !southEast,
                        upWest && upWestColor && !upSouthWest, upEast && upEastColor && !upSouthEast,
                        downWest && downWestColor && !downSouthWest, downEast && downEastColor && !downSouthEast
                    ),
                    new Connections(
                        Direction.WEST, up && !upWest, down && !downWest, north && !northWest, south && !southWest,
                        upNorth && upNorthColor && !upNorthWest, upSouth && upSouthColor && !upSouthWest,
                        downNorth && downNorthColor && !downNorthWest, downSouth && downSouthColor && !downSouthWest
                    ),
                    new Connections(
                        Direction.UP, north && !upNorth, south && !upSouth, west && !upWest, east && !upEast,
                        northWest && northWestColor && !upNorthWest, northEast && northEastColor && !upNorthEast,
                        southWest && southWestColor && !upSouthWest, southEast && southEastColor && !upSouthEast
                    ),
                    new Connections(
                        Direction.DOWN, south && !downSouth, north && !downNorth, west && !downWest, east && !downEast,
                        southWest && southWestColor && !downSouthWest, southEast && southEastColor && !downSouthEast,
                        northWest && northWestColor && !downNorthWest, northEast && northEastColor && !downNorthEast
                    )
                )
            )
            .build();
    }

    private static @Nullable BlockEntity getAntiblockAt(BlockAndTintGetter level, BlockPos pos) {
        if (!level.getBlockState(pos).is(BlockRegistry.RGB_ANTIBLOCK.get())) {
            return null;
        }
        return level.getBlockEntity(pos);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return base.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return base.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return base.getParticleIcon(ModelData.EMPTY);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemTransforms getTransforms() {
        return base.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return base.getOverrides();
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return base.getRenderTypes(state, rand, data);
    }

    public static void unpackPosition(int[] vertexData, float[] pos, int vert) {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        pos[0] = Float.intBitsToFloat(vertexData[offset]);
        pos[1] = Float.intBitsToFloat(vertexData[offset + 1]);
        pos[2] = Float.intBitsToFloat(vertexData[offset + 2]);
    }

    public static void unpackUV(int[] vertexData, float[] uv, int vert) {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        uv[0] = Float.intBitsToFloat(vertexData[offset]);
        uv[1] = Float.intBitsToFloat(vertexData[offset + 1]);
    }

    public static void packPosition(float[] pos, int[] vertexData, int vert) {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
        vertexData[offset] = Float.floatToRawIntBits(pos[0]);
        vertexData[offset + 1] = Float.floatToRawIntBits(pos[1]);
        vertexData[offset + 2] = Float.floatToRawIntBits(pos[2]);
    }

    public static void packUV(float[] uv, int[] vertexData, int vert) {
        int offset = vert * IQuadTransformer.STRIDE + IQuadTransformer.UV0;
        vertexData[offset] = Float.floatToRawIntBits(uv[0]);
        vertexData[offset + 1] = Float.floatToRawIntBits(uv[1]);
    }

    public record Connections(
        Direction side,
        boolean up,
        boolean down,
        boolean left,
        boolean right,
        boolean upLeft,
        boolean upRight,
        boolean downLeft,
        boolean downRight
    ) {
        public boolean isClosed() {
            return !up && !down && !left && !right;
        }
    }

    public static class AntiblockModelData {
        public static final ModelProperty<AntiblockModelData> ANTIBLOCK_MODEL_DATA = new ModelProperty<>();

        public final Map<Direction, Connections> connections = new EnumMap<>(Direction.class);

        public AntiblockModelData(
            Connections north,
            Connections east,
            Connections south,
            Connections west,
            Connections up,
            Connections down
        ) {
            connections.put(Direction.NORTH, north);
            connections.put(Direction.EAST, east);
            connections.put(Direction.SOUTH, south);
            connections.put(Direction.WEST, west);
            connections.put(Direction.UP, up);
            connections.put(Direction.DOWN, down);
        }
    }

    public static class Model implements IUnbakedGeometry<Model> {
        private final BlockModel baseModel;

        public Model(BlockModel baseModel) {
            this.baseModel = baseModel;
        }

        @Override
        public BakedModel bake(
            IGeometryBakingContext owner,
            ModelBaker bakery,
            Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelTransform,
            ItemOverrides overrides,
            ResourceLocation modelLocation
        ) {
            BakedModel bakedBase =
                baseModel.bake(bakery, baseModel, spriteGetter, modelTransform, modelLocation, owner.isGui3d());
            Map<Direction, BakedQuad> bgQuads = new EnumMap<>(Direction.class);
            Map<Direction, BakedQuad> frameQuads = new EnumMap<>(Direction.class);

            RandomSource rand = RandomSource.create();
            for (Direction side : Direction.values()) {
                List<BakedQuad> quads =
                    bakedBase.getQuads(Blocks.STONE.defaultBlockState(), side, rand, ModelData.EMPTY, null);

                for (BakedQuad quad : quads) {
                    // noinspection resource
                    ResourceLocation name = quad.getSprite().contents().name();
                    if (name.equals(new ResourceLocation(RGBBlocks.MOD_ID, "block/white"))) {
                        bgQuads.put(side, quad);
                    } else if (name.equals(new ResourceLocation(RGBBlocks.MOD_ID, "block/antiblock"))) {
                        frameQuads.put(side, quad);
                    }
                }
            }

            return new AntiblockBakedModel(bakedBase, bgQuads, frameQuads);
        }

        @Override
        public void resolveParents(
            Function<ResourceLocation, UnbakedModel> modelGetter,
            IGeometryBakingContext context
        ) {
            baseModel.resolveParents(modelGetter);
        }
    }

    public static class ModelLoader implements IGeometryLoader<Model> {
        @Override
        public Model read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
            BlockModel baseModel = deserializationContext
                .deserialize(GsonHelper.getAsJsonObject(modelContents, "base_model"), BlockModel.class);
            return new Model(baseModel);
        }
    }
}
