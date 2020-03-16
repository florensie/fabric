/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.item.group.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

import net.fabricmc.fabric.impl.item.group.CreativeScreenExtensions;
import net.fabricmc.fabric.impl.item.group.FabricCreativeScreenComponents;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen implements CreativeScreenExtensions {
	public MixinCreativeInventoryScreen(ScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Shadow
	protected abstract void setSelectedTab(ItemGroup itemGroup);

	@Shadow
	public abstract int getSelectedTab(); /* XXX getSelectedTab XXX */

	// "static" matches selectedTab
	private static int fabric_currentPage = 0;

	private int fabric_getPageOffset(int page) {
		switch (page) {
		case 0:
			return 0;
		case 1:
			return 12;
		default:
			return 12 + ((12 - FabricCreativeScreenComponents.COMMON_GROUPS.size()) * (page - 1));
		}
	}

	private int fabric_getOffsetPage(int offset) {
		if (offset < 12) {
			return 0;
		} else {
			return 1 + ((offset - 12) / (12 - FabricCreativeScreenComponents.COMMON_GROUPS.size()));
		}
	}

	@Override
	public void fabric_nextPage() {
		if (fabric_getPageOffset(fabric_currentPage + 1) >= ItemGroup.GROUPS.length) {
			return;
		}

		fabric_currentPage++;
		fabric_updateSelection();
	}

	@Override
	public void fabric_previousPage() {
		if (fabric_currentPage == 0) {
			return;
		}

		fabric_currentPage--;
		fabric_updateSelection();
	}

	@Override
	public boolean fabric_isButtonVisible(FabricCreativeScreenComponents.Type type) {
		return ItemGroup.GROUPS.length > 12;
	}

	@Override
	public boolean fabric_isButtonEnabled(FabricCreativeScreenComponents.Type type) {
		if (type == FabricCreativeScreenComponents.Type.NEXT) {
			return !(fabric_getPageOffset(fabric_currentPage + 1) >= ItemGroup.GROUPS.length);
		}

		if (type == FabricCreativeScreenComponents.Type.PREVIOUS) {
			return fabric_currentPage != 0;
		}

		return false;
	}

	private void fabric_updateSelection() {
		int minPos = fabric_getPageOffset(fabric_currentPage);
		int maxPos = fabric_getPageOffset(fabric_currentPage + 1) - 1;
		int curPos = getSelectedTab();

		if (curPos < minPos || curPos > maxPos) {
			setSelectedTab(ItemGroup.GROUPS[fabric_getPageOffset(fabric_currentPage)]);
		}
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo info) {
		fabric_updateSelection();

		int xpos = x + 170;
		int ypos = y + 4;

		addButton(new FabricCreativeScreenComponents.ItemGroupButtonWidget(xpos + 10, ypos, FabricCreativeScreenComponents.Type.NEXT, this));
		addButton(new FabricCreativeScreenComponents.ItemGroupButtonWidget(xpos, ypos, FabricCreativeScreenComponents.Type.PREVIOUS, this));
	}

	@Inject(method = "setSelectedTab", at = @At("HEAD"), cancellable = true)
	private void setSelectedTab(ItemGroup itemGroup, CallbackInfo info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.cancel();
		}
	}

	@Inject(method = "renderTabTooltipIfHovered", at = @At("HEAD"), cancellable = true)
	private void method_2471(ItemGroup itemGroup, int mx, int my, CallbackInfoReturnable<Boolean> info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "isClickInTab", at = @At("HEAD"), cancellable = true)
	private void isClickInTab(ItemGroup itemGroup, double mx, double my, CallbackInfoReturnable<Boolean> info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "renderTabIcon", at = @At("HEAD"), cancellable = true)
	private void method_2468(ItemGroup itemGroup, CallbackInfo info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.cancel();
		}
	}

	private boolean fabric_isGroupVisible(ItemGroup itemGroup) {
		if (FabricCreativeScreenComponents.COMMON_GROUPS.contains(itemGroup)) {
			return true;
		}

		return fabric_currentPage == fabric_getOffsetPage(itemGroup.getIndex());
	}

	@Override
	public int fabric_currentPage() {
		return fabric_currentPage;
	}
}