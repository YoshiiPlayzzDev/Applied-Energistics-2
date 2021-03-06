/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.client.render.crafting;

import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.client.render.BasicUnbakedModel;
import appeng.core.AppEng;

/**
 * The built-in model for the connected texture crafting cube.
 */
class CraftingCubeModel implements BasicUnbakedModel<CraftingCubeModel> {

    private final static RenderMaterial RING_CORNER = texture("ring_corner");
    private final static RenderMaterial RING_SIDE_HOR = texture("ring_side_hor");
    private final static RenderMaterial RING_SIDE_VER = texture("ring_side_ver");
    private final static RenderMaterial UNIT_BASE = texture("unit_base");
    private final static RenderMaterial LIGHT_BASE = texture("light_base");
    private final static RenderMaterial ACCELERATOR_LIGHT = texture("accelerator_light");
    private final static RenderMaterial STORAGE_1K_LIGHT = texture("1k_storage_light");
    private final static RenderMaterial STORAGE_4K_LIGHT = texture("4k_storage_light");
    private final static RenderMaterial STORAGE_16K_LIGHT = texture("16k_storage_light");
    private final static RenderMaterial STORAGE_64K_LIGHT = texture("64k_storage_light");
    private final static RenderMaterial MONITOR_BASE = texture("monitor_base");
    private final static RenderMaterial MONITOR_LIGHT_DARK = texture("monitor_light_dark");
    private final static RenderMaterial MONITOR_LIGHT_MEDIUM = texture("monitor_light_medium");
    private final static RenderMaterial MONITOR_LIGHT_BRIGHT = texture("monitor_light_bright");

    private final AbstractCraftingUnitBlock.CraftingUnitType type;

    CraftingCubeModel(AbstractCraftingUnitBlock.CraftingUnitType type) {
        this.type = type;
    }

    @Override
    public Stream<RenderMaterial> getAdditionalTextures() {
        return Stream.of(RING_CORNER, RING_SIDE_HOR, RING_SIDE_VER, UNIT_BASE, LIGHT_BASE, ACCELERATOR_LIGHT,
                STORAGE_1K_LIGHT, STORAGE_4K_LIGHT, STORAGE_16K_LIGHT, STORAGE_64K_LIGHT, MONITOR_BASE,
                MONITOR_LIGHT_DARK, MONITOR_LIGHT_MEDIUM, MONITOR_LIGHT_BRIGHT);
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery,
            Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform,
            ItemOverrideList overrides, ResourceLocation modelLocation) {
        // Retrieve our textures and pass them on to the baked model
        TextureAtlasSprite ringCorner = spriteGetter.apply(RING_CORNER);
        TextureAtlasSprite ringSideHor = spriteGetter.apply(RING_SIDE_HOR);
        TextureAtlasSprite ringSideVer = spriteGetter.apply(RING_SIDE_VER);

        switch (this.type) {
            case UNIT:
                return new UnitBakedModel(ringCorner, ringSideHor, ringSideVer, spriteGetter.apply(UNIT_BASE));
            case ACCELERATOR:
            case STORAGE_1K:
            case STORAGE_4K:
            case STORAGE_16K:
            case STORAGE_64K:
                return new LightBakedModel(ringCorner, ringSideHor, ringSideVer, spriteGetter.apply(LIGHT_BASE),
                        getLightTexture(spriteGetter, this.type));
            case MONITOR:
                return new MonitorBakedModel(ringCorner, ringSideHor, ringSideVer, spriteGetter.apply(UNIT_BASE),
                        spriteGetter.apply(MONITOR_BASE), spriteGetter.apply(MONITOR_LIGHT_DARK),
                        spriteGetter.apply(MONITOR_LIGHT_MEDIUM), spriteGetter.apply(MONITOR_LIGHT_BRIGHT));
            default:
                throw new IllegalArgumentException("Unsupported crafting unit type: " + this.type);
        }
    }

    private static TextureAtlasSprite getLightTexture(Function<RenderMaterial, TextureAtlasSprite> textureGetter,
            AbstractCraftingUnitBlock.CraftingUnitType type) {
        switch (type) {
            case ACCELERATOR:
                return textureGetter.apply(ACCELERATOR_LIGHT);
            case STORAGE_1K:
                return textureGetter.apply(STORAGE_1K_LIGHT);
            case STORAGE_4K:
                return textureGetter.apply(STORAGE_4K_LIGHT);
            case STORAGE_16K:
                return textureGetter.apply(STORAGE_16K_LIGHT);
            case STORAGE_64K:
                return textureGetter.apply(STORAGE_64K_LIGHT);
            default:
                throw new IllegalArgumentException("Crafting unit type " + type + " does not use a light texture.");
        }
    }

    private static RenderMaterial texture(String name) {
        return new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE,
                new ResourceLocation(AppEng.MOD_ID, "block/crafting/" + name));
    }
}
