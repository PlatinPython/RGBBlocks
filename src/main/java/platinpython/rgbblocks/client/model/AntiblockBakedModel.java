package platinpython.rgbblocks.client.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import platinpython.rgbblocks.RGBBlocks;
import platinpython.rgbblocks.tileentity.RGBTileEntity;
import platinpython.rgbblocks.util.registries.BlockRegistry;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public class AntiblockBakedModel implements BakedModel {
    private static final int ELEM_POSITION = findElementIndex(DefaultVertexFormat.ELEMENT_POSITION);
    private static final int ELEM_UV0 = findElementIndex(DefaultVertexFormat.ELEMENT_UV0);
    private static final int ELEM_LIGHT = findElementIndex(DefaultVertexFormat.ELEMENT_UV2);

    private final BakedModel base;
    private final Map<Direction, BakedQuad> bgQuads;
    private final Map<Direction, BakedQuad> frameQuads;
    private final LoadingCache<Direction, List<BakedQuad>> bgCache = CacheBuilder.newBuilder()
                                                                                 .build(new CacheLoader<>() {
                                                                                     @Override
                                                                                     public List<BakedQuad> load(
                                                                                             Direction key) {
                                                                                         return genBackgroundQuads(
                                                                                                 bgQuads.get(key), key);
                                                                                     }
                                                                                 });
    private final LoadingCache<Connections, List<BakedQuad>> quadCache = CacheBuilder.newBuilder()
                                                                                     .build(new CacheLoader<>() {
                                                                                         @Override
                                                                                         public List<BakedQuad> load(
                                                                                                 Connections key) {
                                                                                             if (key.isClosed()) {
                                                                                                 return List.of(
                                                                                                         bgQuads.get(
                                                                                                                 key.side),
                                                                                                         frameQuads.get(
                                                                                                                 key.side)
                                                                                                 );
                                                                                             }
                                                                                             return genCtmQuads(
                                                                                                     frameQuads.get(
                                                                                                             key.side),
                                                                                                     bgCache.getUnchecked(
                                                                                                             key.side),
                                                                                                     key
                                                                                             );
                                                                                         }
                                                                                     });

    public AntiblockBakedModel(BakedModel baseModel, Map<Direction, BakedQuad> bgQuads,
                               Map<Direction, BakedQuad> frameQuads) {
        this.base = baseModel;
        this.bgQuads = bgQuads;
        this.frameQuads = frameQuads;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random random) {
        return this.getQuads(state, side, random, EmptyModelData.INSTANCE);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random random,
                                    @NotNull IModelData extraData) {
        if (side == null) return Collections.emptyList();

        List<BakedQuad> quads = new ArrayList<>();

        AntiblockModelData modelData = extraData.getData(AntiblockModelData.ANTIBLOCK_MODEL_DATA);
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

    private static BakedQuad genBackgroundQuad(BakedQuad quad, boolean down, boolean right, TextureAtlasSprite sprite,
                                               Direction side) {
        int[] vertexData = cloneVertexData(quad);

        float minX = right ? .5F : 0F;
        float minY = down ? 0F : .5F;
        if (side == Direction.UP) minY = (minY + .5F) % 1F;
        cutQuad(vertexData, minX, minY, minX + .5F, minY + .5F, side);

        int u = right ? 8 : 0;
        int v = down ? 8 : 0;
        applyUVs(vertexData, true, sprite, u, v, side);

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
    }

    private static List<BakedQuad> genCtmQuads(BakedQuad frameQuad, List<BakedQuad> bgQuads, Connections connections) {
        TextureAtlasSprite sprite = frameQuad.getSprite();
        TextureAtlasSprite ctmSprite = Minecraft.getInstance()
                                                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                                                .apply(new ResourceLocation(RGBBlocks.MOD_ID,
                                                                            "block/antiblock_ctm"
                                                ));

        List<BakedQuad> quads = new ArrayList<>(bgQuads);

        Direction side = connections.side;

        quads.add(genCtmQuad(frameQuad, connections.left, connections.up, connections.upLeft, false, false, 0F, .5F,
                             sprite, ctmSprite, side
        ));
        quads.add(genCtmQuad(frameQuad, connections.left, connections.down, connections.downLeft, true, false, 0F, 0F,
                             sprite, ctmSprite, side
        ));
        quads.add(genCtmQuad(frameQuad, connections.right, connections.up, connections.upRight, false, true, .5F, .5F,
                             sprite, ctmSprite, side
        ));
        quads.add(genCtmQuad(frameQuad, connections.right, connections.down, connections.downRight, true, true, .5F, 0F,
                             sprite, ctmSprite, side
        ));

        return quads;
    }

    private static BakedQuad genCtmQuad(BakedQuad quad, boolean x, boolean y, boolean xy, boolean down, boolean right,
                                        float minX, float minY, TextureAtlasSprite defSprite,
                                        TextureAtlasSprite ctmSprite, Direction side) {
        int[] vertexData = cloneVertexData(quad);
        if (side == Direction.UP) minY = (minY + .5F) % 1F;
        cutQuad(vertexData, minX, minY, minX + .5F, minY + .5F, side);

        boolean closed = !x && !y;
        TextureAtlasSprite sprite = closed ? defSprite : ctmSprite;
        int u = getU(x, y, xy) + (right ? (closed ? 8 : 4) : 0);
        int v = getV(x, y, xy) + (down ? (closed ? 8 : 4) : 0);
        applyUVs(vertexData, closed, sprite, u, v, side);

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), sprite, quad.isShade());
    }

    private static int[] cloneVertexData(BakedQuad quad) {
        int size = quad.getVertices().length;
        int[] vertexData = new int[size];
        System.arraycopy(quad.getVertices(), 0, vertexData, 0, size);
        return vertexData;
    }

    private static int getU(boolean x, boolean y, boolean xy) {
        if (!x && !y) {
            return 0;
        }

        if (x && !y) {
            return 0;
        } else if (!x && y) {
            return 8;
        } else {
            return xy ? 0 : 8;
        }
    }

    private static int getV(boolean x, boolean y, boolean xy) {
        if (!x && !y) {
            return 0;
        }

        if (x && !y) {
            return 8;
        } else if (!x && y) {
            return 0;
        } else {
            return xy ? 0 : 8;
        }
    }

    private static void cutQuad(int[] vertexData, float minX, float minY, float maxX, float maxY, Direction side) {
        float[][] pos = new float[4][3];
        for (int i = 0; i < 4; i++) {
            LightUtil.unpack(vertexData, pos[i], DefaultVertexFormat.BLOCK, i, ELEM_POSITION);
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
            LightUtil.pack(pos[i], vertexData, DefaultVertexFormat.BLOCK, i, ELEM_POSITION);
        }
    }

    private static void applyUVs(int[] vertexData, boolean closed, TextureAtlasSprite sprite, int u0, int v0,
                                 Direction side) {
        float uMin = sprite.getU(u0);
        float vMin = sprite.getV(v0);
        float uMax = sprite.getU(u0 + (closed ? 8 : 4));
        float vMax = sprite.getV(v0 + (closed ? 8 : 4));

        float[][] uv = new float[4][2];
        for (int i = 0; i < 4; i++) {
            LightUtil.unpack(vertexData, uv[i], DefaultVertexFormat.BLOCK, i, ELEM_UV0);
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
            LightUtil.pack(uv[i], vertexData, DefaultVertexFormat.BLOCK, i, ELEM_UV0);
        }
    }

    @NotNull
    @Override
    public IModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state,
                                   @NotNull IModelData modelData) {
        if (modelData == EmptyModelData.INSTANCE)
            modelData = new ModelDataMap.Builder().withProperty(AntiblockModelData.ANTIBLOCK_MODEL_DATA).build();

        if (!(getAntiblockAt(level, pos) instanceof RGBTileEntity blockEntity)) return modelData;

        boolean north = false;
        boolean east = false;
        boolean south = false;
        boolean west = false;
        boolean up = false;
        boolean down = false;

        if (getAntiblockAt(level, pos.north()) instanceof RGBTileEntity otherBlockEntity)
            north = otherBlockEntity.getColor() == blockEntity.getColor();
        if (getAntiblockAt(level, pos.east()) instanceof RGBTileEntity otherBlockEntity)
            east = otherBlockEntity.getColor() == blockEntity.getColor();
        if (getAntiblockAt(level, pos.south()) instanceof RGBTileEntity otherBlockEntity)
            south = otherBlockEntity.getColor() == blockEntity.getColor();
        if (getAntiblockAt(level, pos.west()) instanceof RGBTileEntity otherBlockEntity)
            west = otherBlockEntity.getColor() == blockEntity.getColor();
        if (getAntiblockAt(level, pos.above()) instanceof RGBTileEntity otherBlockEntity)
            up = otherBlockEntity.getColor() == blockEntity.getColor();
        if (getAntiblockAt(level, pos.below()) instanceof RGBTileEntity otherBlockEntity)
            down = otherBlockEntity.getColor() == blockEntity.getColor();
        boolean upNorth = getAntiblockAt(level, pos.above().north()) instanceof RGBTileEntity;
        boolean upEast = getAntiblockAt(level, pos.above().east()) instanceof RGBTileEntity;
        boolean upSouth = getAntiblockAt(level, pos.above().south()) instanceof RGBTileEntity;
        boolean upWest = getAntiblockAt(level, pos.above().west()) instanceof RGBTileEntity;
        boolean northEast = getAntiblockAt(level, pos.north().east()) instanceof RGBTileEntity;
        boolean southEast = getAntiblockAt(level, pos.south().east()) instanceof RGBTileEntity;
        boolean southWest = getAntiblockAt(level, pos.south().west()) instanceof RGBTileEntity;
        boolean northWest = getAntiblockAt(level, pos.north().west()) instanceof RGBTileEntity;
        boolean downNorth = getAntiblockAt(level, pos.below().north()) instanceof RGBTileEntity;
        boolean downEast = getAntiblockAt(level, pos.below().east()) instanceof RGBTileEntity;
        boolean downSouth = getAntiblockAt(level, pos.below().south()) instanceof RGBTileEntity;
        boolean downWest = getAntiblockAt(level, pos.below().west()) instanceof RGBTileEntity;
        boolean upNorthEast = getAntiblockAt(level, pos.above().north().east()) instanceof RGBTileEntity;
        boolean upSouthEast = getAntiblockAt(level, pos.above().south().east()) instanceof RGBTileEntity;
        boolean upSouthWest = getAntiblockAt(level, pos.above().south().west()) instanceof RGBTileEntity;
        boolean upNorthWest = getAntiblockAt(level, pos.above().north().west()) instanceof RGBTileEntity;
        boolean downNorthEast = getAntiblockAt(level, pos.below().north().east()) instanceof RGBTileEntity;
        boolean downSouthEast = getAntiblockAt(level, pos.below().south().east()) instanceof RGBTileEntity;
        boolean downSouthWest = getAntiblockAt(level, pos.below().south().west()) instanceof RGBTileEntity;
        boolean downNorthWest = getAntiblockAt(level, pos.below().north().west()) instanceof RGBTileEntity;

        modelData.setData(AntiblockModelData.ANTIBLOCK_MODEL_DATA, new AntiblockModelData(
                new Connections(Direction.NORTH, up && !upNorth, down && !downNorth, east && !northEast,
                                west && !northWest, upEast && !upNorthEast, upWest && !upNorthWest,
                                downEast && !downNorthEast, downWest && !downNorthWest
                ), new Connections(Direction.EAST, up && !upEast, down && !downEast, south && !southEast,
                                   north && !northEast, upSouth && !upSouthEast, upNorth && !upNorthEast,
                                   downSouth && !downSouthEast, downNorth && !downNorthEast
        ), new Connections(Direction.SOUTH, up && !upSouth, down && !downSouth, west && !southWest, east && !southEast,
                           upWest && !upSouthWest, upEast && !upSouthEast, downWest && !downSouthWest,
                           downEast && !downSouthEast
        ), new Connections(Direction.WEST, up && !upWest, down && !downWest, north && !northWest, south && !southWest,
                           upNorth && !upNorthWest, upSouth && !upSouthWest, downNorth && !downNorthWest,
                           downSouth && !downSouthWest
        ), new Connections(Direction.UP, north && !upNorth, south && !upSouth, west && !upWest, east && !upEast,
                           northWest && !upNorthWest, northEast && !upNorthEast, southWest & !upSouthWest,
                           southEast && !upSouthEast
        ), new Connections(Direction.DOWN, south && !downSouth, north && !downNorth, west && !downWest,
                           east && !downEast, southWest && !downSouthWest, southEast && !downSouthEast,
                           northWest && !downNorthWest, northEast && !downNorthEast
        )));
        return modelData;
    }

    private static BlockEntity getAntiblockAt(BlockAndTintGetter level, BlockPos pos) {
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
        return base.getParticleIcon(EmptyModelData.INSTANCE);
    }

    @Override
    public ItemTransforms getTransforms() {
        return base.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return base.getOverrides();
    }

    private static int findElementIndex(VertexFormatElement element) {
        List<VertexFormatElement> elements = DefaultVertexFormat.BLOCK.getElements();
        for (int i = 0; i < elements.size(); i++) {
            VertexFormatElement elem = elements.get(i);
            if (elem == element) {
                return i;
            }
        }
        throw new IllegalArgumentException(
                String.format("VertexFormat %s doesn't have element %s", DefaultVertexFormat.BLOCK, element));
    }

    public record Connections(Direction side, boolean up, boolean down, boolean left, boolean right, boolean upLeft,
                              boolean upRight, boolean downLeft, boolean downRight) {
        public boolean isClosed() {
            return !up && !down && !left && !right;
        }
    }

    public static class AntiblockModelData {
        public static final ModelProperty<AntiblockModelData> ANTIBLOCK_MODEL_DATA = new ModelProperty<>();

        public final Map<Direction, Connections> connections = new EnumMap<>(Direction.class);

        public AntiblockModelData(Connections north, Connections east, Connections south, Connections west,
                                  Connections up, Connections down) {
            connections.put(Direction.NORTH, north);
            connections.put(Direction.EAST, east);
            connections.put(Direction.SOUTH, south);
            connections.put(Direction.WEST, west);
            connections.put(Direction.UP, up);
            connections.put(Direction.DOWN, down);
        }
    }

    public static class Model implements IModelGeometry<Model> {
        private final BlockModel baseModel;

        public Model(BlockModel baseModel) {
            this.baseModel = baseModel;
        }

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery,
                               Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                               ItemOverrides overrides, ResourceLocation modelLocation) {

            BakedModel bakedBase = baseModel.bake(bakery, baseModel, spriteGetter, modelTransform, modelLocation,
                                                  owner.isShadedInGui()
            );
            Map<Direction, BakedQuad> bgQuads = new EnumMap<>(Direction.class);
            Map<Direction, BakedQuad> frameQuads = new EnumMap<>(Direction.class);

            Random rand = new Random();
            for (Direction side : Direction.values()) {
                List<BakedQuad> quads = bakedBase.getQuads(Blocks.STONE.defaultBlockState(), side, rand,
                                                           EmptyModelData.INSTANCE
                );

                for (BakedQuad quad : quads) {
                    if (quad.getSprite().getName().equals(new ResourceLocation(RGBBlocks.MOD_ID, "block/white"))) {
                        int[] vertexData = quad.getVertices();

                        float[] light = new float[2];
                        for (int i = 0; i < 4; i++) {
                            LightUtil.unpack(vertexData, light, DefaultVertexFormat.BLOCK, i, ELEM_LIGHT);
                            light[0] = light[1] = (float) LightTexture.FULL_BRIGHT / (float) Short.MAX_VALUE;
                            LightUtil.pack(light, vertexData, DefaultVertexFormat.BLOCK, i, ELEM_LIGHT);
                        }

                        bgQuads.put(side, new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(),
                                                        quad.getSprite(), false
                        ));
                    } else if (quad.getSprite()
                                   .getName()
                                   .equals(new ResourceLocation(RGBBlocks.MOD_ID, "block/antiblock"))) {
                        frameQuads.put(side, quad);
                    }
                }
            }

            return new AntiblockBakedModel(bakedBase, bgQuads, frameQuads);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner,
                                                Function<ResourceLocation, UnbakedModel> modelGetter,
                                                Set<Pair<String, String>> missingTextureErrors) {
            Set<Material> textures = Sets.newHashSet();

            textures.addAll(baseModel.getMaterials(modelGetter, missingTextureErrors));
            textures.add(new Material(InventoryMenu.BLOCK_ATLAS,
                                      new ResourceLocation(RGBBlocks.MOD_ID, "block/antiblock_ctm")
            ));

            return textures;
        }
    }

    public static class ModelLoader implements IModelLoader<Model> {
        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {

        }

        @Override
        public Model read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            BlockModel baseModel = deserializationContext.deserialize(
                    GsonHelper.getAsJsonObject(modelContents, "base_model"), BlockModel.class);
            return new Model(baseModel);
        }
    }
}