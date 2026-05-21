package org.maia.amstrad.gui.browser.carousel.animation.startup.ninja;

import java.util.HashSet;
import java.util.Set;

import org.maia.amstrad.gui.sprite.SpriteImageCatalog;

public class NinjaAnimationCatalog {

	private Set<NinjaAnimation> inGameAnimations;

	private NinjaAnimation freezeAnimation = new FreezeAnimation();

	private NinjaAnimation turnAnimation = new TurnAnimation();

	private NinjaAnimation bowAnimation = new BowAnimation();

	private NinjaAnimation stepForwardAnimation = new StepForwardAnimation();

	private NinjaAnimation stepBackwardAnimation = new StepBackwardAnimation();

	private NinjaAnimation jumpAnimation = new JumpAnimation();

	private NinjaAnimation jumpRollAnimation = new JumpRollAnimation();

	private NinjaAnimation backFlipAnimation = new BackFlipAnimation();

	private NinjaAnimation spreadAnimation = new SpreadAnimation();

	private NinjaAnimation unspreadAnimation = new UnspreadAnimation();

	private NinjaAnimation croucheAnimation = new CroucheAnimation();

	private NinjaAnimation riseAnimation = new RiseAnimation();

	private NinjaAnimation punchAnimation = new PunchAnimation();

	private NinjaAnimation kickAnimation = new KickAnimation();

	private NinjaAnimation punchKickAnimation = new PunchKickAnimation();

	private NinjaAnimation crouchePunchAnimation = new CrouchePunchAnimation();

	private NinjaAnimation spreadPunchAnimation = new SpreadPunchAnimation();

	private NinjaPose standardPose;

	private NinjaPose croucheMidwayPose;

	private NinjaPose crouchePose;

	private NinjaPose crouchePunchPose;

	private NinjaPose punchPose;

	private NinjaPose kickPose;

	private NinjaPose punchKickPose;

	private NinjaPose jumpMidwayPose;

	private NinjaPose jumpHighPose;

	private NinjaPose jumpRoll1Pose;

	private NinjaPose jumpRoll2Pose;

	private NinjaPose jumpRoll3Pose;

	private NinjaPose jumpRoll4Pose;

	private NinjaPose bowPose;

	private NinjaPose stepPose;

	private NinjaPose spreadPose;

	private NinjaPose spreadPunchPose;

	private SpriteImageCatalog spriteImageCatalog;

	public NinjaAnimationCatalog(SpriteImageCatalog spriteImageCatalog) {
		this.spriteImageCatalog = spriteImageCatalog;
	}

	public Set<NinjaAnimation> getInGameAnimations() {
		if (inGameAnimations == null) {
			inGameAnimations = new HashSet<NinjaAnimation>();
			inGameAnimations.add(getFreezeAnimation());
			inGameAnimations.add(getStepForwardAnimation());
			inGameAnimations.add(getStepBackwardAnimation());
			inGameAnimations.add(getJumpAnimation());
			inGameAnimations.add(getJumpRollAnimation());
			inGameAnimations.add(getBackFlipAnimation());
			inGameAnimations.add(getSpreadAnimation());
			inGameAnimations.add(getUnspreadAnimation());
			inGameAnimations.add(getCroucheAnimation());
			inGameAnimations.add(getRiseAnimation());
			inGameAnimations.add(getPunchAnimation());
			inGameAnimations.add(getKickAnimation());
			inGameAnimations.add(getPunchKickAnimation());
			inGameAnimations.add(getCrouchePunchAnimation());
			inGameAnimations.add(getSpreadPunchAnimation());
		}
		return inGameAnimations;
	}

	public NinjaAnimation getFreezeAnimation() {
		return freezeAnimation;
	}

	public NinjaAnimation getTurnAnimation() {
		return turnAnimation;
	}

	public NinjaAnimation getBowAnimation() {
		return bowAnimation;
	}

	public NinjaAnimation getStepForwardAnimation() {
		return stepForwardAnimation;
	}

	public NinjaAnimation getStepBackwardAnimation() {
		return stepBackwardAnimation;
	}

	public NinjaAnimation getJumpAnimation() {
		return jumpAnimation;
	}

	public NinjaAnimation getJumpRollAnimation() {
		return jumpRollAnimation;
	}

	public NinjaAnimation getBackFlipAnimation() {
		return backFlipAnimation;
	}

	public NinjaAnimation getSpreadAnimation() {
		return spreadAnimation;
	}

	public NinjaAnimation getUnspreadAnimation() {
		return unspreadAnimation;
	}

	public NinjaAnimation getCroucheAnimation() {
		return croucheAnimation;
	}

	public NinjaAnimation getRiseAnimation() {
		return riseAnimation;
	}

	public NinjaAnimation getPunchAnimation() {
		return punchAnimation;
	}

	public NinjaAnimation getKickAnimation() {
		return kickAnimation;
	}

	public NinjaAnimation getPunchKickAnimation() {
		return punchKickAnimation;
	}

	public NinjaAnimation getCrouchePunchAnimation() {
		return crouchePunchAnimation;
	}

	public NinjaAnimation getSpreadPunchAnimation() {
		return spreadPunchAnimation;
	}

	public NinjaPose getStandardPose() {
		if (standardPose == null) {
			standardPose = new NinjaPose(getSpriteImageCatalog().getNinja());
		}
		return standardPose;
	}

	public NinjaPose getCroucheMidwayPose() {
		if (croucheMidwayPose == null) {
			croucheMidwayPose = new NinjaPose(getSpriteImageCatalog().getNinjaCroucheMidway());
		}
		return croucheMidwayPose;
	}

	public NinjaPose getCrouchePose() {
		if (crouchePose == null) {
			crouchePose = new NinjaPose(getSpriteImageCatalog().getNinjaCrouche());
		}
		return crouchePose;
	}

	public NinjaPose getCrouchePunchPose() {
		if (crouchePunchPose == null) {
			crouchePunchPose = new NinjaPose(getSpriteImageCatalog().getNinjaCrouchePunch());
		}
		return crouchePunchPose;
	}

	public NinjaPose getPunchPose() {
		if (punchPose == null) {
			punchPose = new NinjaPose(getSpriteImageCatalog().getNinjaPunch());
		}
		return punchPose;
	}

	public NinjaPose getKickPose() {
		if (kickPose == null) {
			kickPose = new NinjaPose(getSpriteImageCatalog().getNinjaKick());
		}
		return kickPose;
	}

	public NinjaPose getPunchKickPose() {
		if (punchKickPose == null) {
			punchKickPose = new NinjaPose(getSpriteImageCatalog().getNinjaPunchKick());
		}
		return punchKickPose;
	}

	public NinjaPose getJumpMidwayPose() {
		if (jumpMidwayPose == null) {
			jumpMidwayPose = new NinjaPose(getSpriteImageCatalog().getNinjaCroucheMidway(), 0, 0, -3);
		}
		return jumpMidwayPose;
	}

	public NinjaPose getJumpHighPose() {
		if (jumpHighPose == null) {
			jumpHighPose = new NinjaPose(getSpriteImageCatalog().getNinjaCrouche(), 0, 0, -6);
		}
		return jumpHighPose;
	}

	public NinjaPose getJumpRoll1Pose() {
		if (jumpRoll1Pose == null) {
			jumpRoll1Pose = new NinjaPose(getSpriteImageCatalog().getNinjaRoll1(), 0, 0, -3);
		}
		return jumpRoll1Pose;
	}

	public NinjaPose getJumpRoll2Pose() {
		if (jumpRoll2Pose == null) {
			jumpRoll2Pose = new NinjaPose(getSpriteImageCatalog().getNinjaRoll2(), 0, 0, -9);
		}
		return jumpRoll2Pose;
	}

	public NinjaPose getJumpRoll3Pose() {
		if (jumpRoll3Pose == null) {
			jumpRoll3Pose = new NinjaPose(getSpriteImageCatalog().getNinjaRoll3(), 0, 0, -7);
		}
		return jumpRoll3Pose;
	}

	public NinjaPose getJumpRoll4Pose() {
		if (jumpRoll4Pose == null) {
			jumpRoll4Pose = new NinjaPose(getSpriteImageCatalog().getNinjaRoll4(), 0, 0, -1);
		}
		return jumpRoll4Pose;
	}

	public NinjaPose getBowPose() {
		if (bowPose == null) {
			bowPose = new NinjaPose(getSpriteImageCatalog().getNinjaRoll1(), 2, -10, 0);
		}
		return bowPose;
	}

	public NinjaPose getStepPose() {
		if (stepPose == null) {
			stepPose = new NinjaPose(getSpriteImageCatalog().getNinjaSpread());
		}
		return stepPose;
	}

	public NinjaPose getSpreadPose() {
		if (spreadPose == null) {
			spreadPose = new NinjaPose(getSpriteImageCatalog().getNinjaSpread());
		}
		return spreadPose;
	}

	public NinjaPose getSpreadPunchPose() {
		if (spreadPunchPose == null) {
			spreadPunchPose = new NinjaPose(getSpriteImageCatalog().getNinjaSpreadPunch());
		}
		return spreadPunchPose;
	}

	private SpriteImageCatalog getSpriteImageCatalog() {
		return spriteImageCatalog;
	}

	private class FreezeAnimation extends NinjaAnimation {

		public FreezeAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			// nothing
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return true;
		}

		@Override
		public long getDurationMillis() {
			return 200L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return ninja.getLook();
		}

		@Override
		public String getName() {
			return "FREEZE";
		}

	}

	private class TurnAnimation extends NinjaAnimation {

		public TurnAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			if (unitTime == 1f) {
				changeLook(ninja, ninja.getLook().getMirroredLook());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.getLook().getMirroredLook() != null;
		}

		@Override
		public long getDurationMillis() {
			return 100L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return ninja.getLook().getMirroredLook();
		}

		@Override
		public String getName() {
			return "TURN";
		}

	}

	private class BowAnimation extends NinjaAnimation {

		public BowAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			if (unitTime <= 0.5f) {
				changePose(ninja, getBowPose());
			} else {
				changePose(ninja, getStandardPose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getStandardPose());
		}

		@Override
		public long getDurationMillis() {
			return 600L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getStandardPose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "BOW";
		}

	}

	private class StepForwardAnimation extends NinjaAnimation {

		public StepForwardAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			move(ninja, Math.round(unitTime * getAdvancementX(ninja)), 0);
			if (unitTime <= 0.25f || unitTime >= 0.75f) {
				changePose(ninja, getStandardPose());
			} else {
				changePose(ninja, getStepPose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getStandardPose());
		}

		@Override
		public int getAdvancementX(Ninja ninja) {
			return 6 * (ninja.isRightFacing() ? 1 : -1);
		}

		@Override
		public long getDurationMillis() {
			return 100L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getStandardPose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "STEP_FORWARD";
		}

	}

	private class StepBackwardAnimation extends StepForwardAnimation {

		public StepBackwardAnimation() {
		}

		@Override
		public int getAdvancementX(Ninja ninja) {
			return -super.getAdvancementX(ninja);
		}

		@Override
		public String getName() {
			return "STEP_BACKWARD";
		}

	}

	private class JumpAnimation extends NinjaAnimation {

		public JumpAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			move(ninja, 0, -Math.round(26f * (1f - (unitTime * 2f - 1f) * (unitTime * 2f - 1f))));
			if (unitTime <= 0.1f || unitTime >= 0.9f) {
				changePose(ninja, getStandardPose());
			} else if (unitTime <= 0.3f || unitTime >= 0.7f) {
				changePose(ninja, getJumpMidwayPose());
			} else {
				changePose(ninja, getJumpHighPose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getStandardPose());
		}

		@Override
		public boolean isJump() {
			return true;
		}

		@Override
		public long getDurationMillis() {
			return 400L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getStandardPose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "JUMP";
		}

	}

	private class JumpRollAnimation extends NinjaAnimation {

		public JumpRollAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			move(ninja, Math.round(unitTime * getAdvancementX(ninja)),
					-Math.round(30f * (1f - (unitTime * 2f - 1f) * (unitTime * 2f - 1f))));
			if (unitTime < 0.25f) {
				changePose(ninja, getJumpRoll1Pose());
			} else if (unitTime < 0.5f) {
				changePose(ninja, getJumpRoll2Pose());
			} else if (unitTime < 0.75f) {
				changePose(ninja, getJumpRoll3Pose());
			} else if (unitTime < 1f) {
				changePose(ninja, getJumpRoll4Pose());
			} else {
				changePose(ninja, getStandardPose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getStandardPose());
		}

		@Override
		public boolean isJump() {
			return true;
		}

		@Override
		public int getAdvancementX(Ninja ninja) {
			return 60 * (ninja.isRightFacing() ? 1 : -1);
		}

		@Override
		public long getDurationMillis() {
			return 600L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getStandardPose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "JUMP_ROLL";
		}

	}

	private class BackFlipAnimation extends NinjaAnimation {

		public BackFlipAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			move(ninja, Math.round(unitTime * getAdvancementX(ninja)),
					-Math.round(6f * (1f - (unitTime * 2f - 1f) * (unitTime * 2f - 1f))));
			if (unitTime <= 0.2f) {
				changePose(ninja, getJumpRoll4Pose());
			} else if (unitTime <= 0.4f) {
				changePose(ninja, getJumpRoll3Pose());
			} else if (unitTime <= 0.6f) {
				changePose(ninja, getJumpRoll2Pose());
			} else if (unitTime <= 0.8f) {
				changePose(ninja, getJumpRoll1Pose());
			} else {
				changePose(ninja, getStandardPose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getStandardPose());
		}

		@Override
		public boolean isJump() {
			return true;
		}

		@Override
		public int getAdvancementX(Ninja ninja) {
			return 15 * (ninja.isRightFacing() ? -1 : 1);
		}

		@Override
		public long getDurationMillis() {
			return 400L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getStandardPose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "BACK_FLIP";
		}

	}

	private class SpreadAnimation extends NinjaAnimation {

		public SpreadAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			if (unitTime <= 0.5f) {
				changePose(ninja, getStandardPose());
			} else {
				changePose(ninja, getSpreadPose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getStandardPose());
		}

		@Override
		public long getDurationMillis() {
			return 150L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getSpreadPose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "SPREAD";
		}

	}

	private class UnspreadAnimation extends NinjaAnimation {

		public UnspreadAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			if (unitTime <= 0.5f) {
				changePose(ninja, getSpreadPose());
			} else {
				changePose(ninja, getStandardPose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getSpreadPose());
		}

		@Override
		public long getDurationMillis() {
			return 150L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getStandardPose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "UNSPREAD";
		}

	}

	private class CroucheAnimation extends NinjaAnimation {

		public CroucheAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			if (unitTime <= 0.4f) {
				changePose(ninja, getStandardPose());
			} else if (unitTime <= 0.8f) {
				changePose(ninja, getCroucheMidwayPose());
			} else {
				changePose(ninja, getCrouchePose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getStandardPose());
		}

		@Override
		public long getDurationMillis() {
			return 150L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getCrouchePose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "CROUCHE";
		}

	}

	private class RiseAnimation extends NinjaAnimation {

		public RiseAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			if (unitTime <= 0.4f) {
				changePose(ninja, getCrouchePose());
			} else if (unitTime <= 0.8f) {
				changePose(ninja, getCroucheMidwayPose());
			} else {
				changePose(ninja, getStandardPose());
			}
		}

		@Override
		public boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getCrouchePose());
		}

		@Override
		public long getDurationMillis() {
			return 150L;
		}

		@Override
		public NinjaLook getEndLook(Ninja ninja) {
			return getStandardPose().getLook(ninja.getOrientation());
		}

		@Override
		public String getName() {
			return "RISE";
		}

	}

	private abstract class CombatAnimation extends NinjaAnimation {

		protected CombatAnimation() {
		}

		@Override
		protected void animateNinja(Ninja ninja, float unitTime) {
			if (unitTime <= 0.25f || unitTime >= 0.75f) {
				changePose(ninja, getBasePose());
			} else {
				changePose(ninja, getCombatPose());
			}
		}

		protected abstract NinjaPose getBasePose();

		protected abstract NinjaPose getCombatPose();

		@Override
		public final boolean isApplicable(Ninja ninja) {
			return ninja.hasPose(getBasePose());
		}

		@Override
		public final boolean isCombative() {
			return true;
		}

		@Override
		public long getDurationMillis() {
			return 250L;
		}

		@Override
		public final NinjaLook getEndLook(Ninja ninja) {
			return getBasePose().getLook(ninja.getOrientation());
		}

	}

	private class PunchAnimation extends CombatAnimation {

		public PunchAnimation() {
		}

		@Override
		protected NinjaPose getBasePose() {
			return getStandardPose();
		}

		@Override
		protected NinjaPose getCombatPose() {
			return getPunchPose();
		}

		@Override
		public String getName() {
			return "PUNCH";
		}

	}

	private class KickAnimation extends CombatAnimation {

		public KickAnimation() {
		}

		@Override
		protected NinjaPose getBasePose() {
			return getStandardPose();
		}

		@Override
		protected NinjaPose getCombatPose() {
			return getKickPose();
		}

		@Override
		public String getName() {
			return "KICK";
		}

	}

	private class PunchKickAnimation extends CombatAnimation {

		public PunchKickAnimation() {
		}

		@Override
		protected NinjaPose getBasePose() {
			return getStandardPose();
		}

		@Override
		protected NinjaPose getCombatPose() {
			return getPunchKickPose();
		}

		@Override
		public String getName() {
			return "PUNCH_KICK";
		}

	}

	private class CrouchePunchAnimation extends CombatAnimation {

		public CrouchePunchAnimation() {
		}

		@Override
		protected NinjaPose getBasePose() {
			return getCrouchePose();
		}

		@Override
		protected NinjaPose getCombatPose() {
			return getCrouchePunchPose();
		}

		@Override
		public String getName() {
			return "CROUCHE_PUNCH";
		}

	}

	private class SpreadPunchAnimation extends CombatAnimation {

		public SpreadPunchAnimation() {
		}

		@Override
		protected NinjaPose getBasePose() {
			return getSpreadPose();
		}

		@Override
		protected NinjaPose getCombatPose() {
			return getSpreadPunchPose();
		}

		@Override
		public String getName() {
			return "SPREAD_PUNCH";
		}

	}

}