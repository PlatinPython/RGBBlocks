package platinpython.rgbblocks.client.model;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;

public class FullbrightBakedModel implements IBakedModel {
	private final IBakedModel base;

	public FullbrightBakedModel(IBakedModel base) {
		this.base = base;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState blockState, Direction direction, Random random) {
		List<BakedQuad> quads = this.base.getQuads(blockState, direction, random, EmptyModelData.INSTANCE);
		for (int i = 0; i < quads.size(); i++) {
			BakedQuad quad = quads.get(i);
			int[] vertexData = quad.getVertices();
			for (int j = 0; j < 4; j++) {
				vertexData[8 * j + 6] = 0x00F000F0;
			}
			quads.set(i, new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(),
					quad.isShade()));
		}
		return quads;
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
		return base.getParticleTexture(EmptyModelData.INSTANCE);
	}

	@Override
	public ItemOverrideList getOverrides() {
		return base.getOverrides();
	}
}
