package awildgoose.gooselights.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class SpotlightBlock extends BlockWithEntity {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public SpotlightBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(LIT, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(SpotlightBlock::new);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean powered = ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos());
        return this.getDefaultState().with(LIT, powered);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient) {
            boolean lit = state.get(LIT);
            boolean powered = world.isReceivingRedstonePower(pos);

            if (lit != powered) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof SpotlightBlockEntity blockEntity) {
                    if (lit) blockEntity.removeLight();
                    else blockEntity.addLight(pos);
                }

                world.setBlockState(pos, state.with(LIT, powered), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof SpotlightBlockEntity be)) {
            return super.onUse(state, world, pos, player, hit);
        }

        if (player.isSneaking())
            be.addAlignY();
        else
            be.addAlignX();

        return ActionResult.SUCCESS;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof SpotlightBlockEntity be)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        if (stack.getItem() instanceof DyeItem dye) {
            DyeColor dyeColor = dye.getColor();
            int rgbInt = dyeColor.getEntityColor();

            float r = ((rgbInt >> 16) & 0xFF) / 255f;
            float g = ((rgbInt >> 8) & 0xFF) / 255f;
            float b = (rgbInt & 0xFF) / 255f;

            be.setColor(new Color(r, g, b));

            if (!player.isCreative()) stack.decrement(1);

            return ActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    public static int getLuminance(BlockState currentBlockState) {
        return 0;//currentBlockState.get(SpotlightBlock.LIT) ? 15 : 0;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpotlightBlockEntity(pos, state);
    }
}
