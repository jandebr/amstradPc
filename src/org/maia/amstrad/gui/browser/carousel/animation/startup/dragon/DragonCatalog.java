package org.maia.amstrad.gui.browser.carousel.animation.startup.dragon;

import org.maia.amstrad.gui.browser.carousel.animation.startup.dragon.CarouselDragonFightAnimation.Projectile;
import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.SpriteImageRLE;
import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;
import org.maia.amstrad.gui.sprite.animation.SpriteAnimation;
import org.maia.amstrad.gui.sprite.animation.SpriteLook;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D.ControlValueGenerator;
import org.maia.graphics2d.geometry.Point2D;
import org.maia.graphics2d.geometry.Vector2D;

public class DragonCatalog {

	private DragonAnimation wingedAnimation = new WingedAnimation();

	private DragonAnimation ascendingWingedAnimation = new AscendingWingedAnimation();

	private DragonAnimation freeFallAnimation = new FreeFallAnimation();

	private DragonAnimation floatingAnimation = new FloatingAnimation();

	private DragonAnimation fallingDeadAnimation = new FallingDeadAnimation();

	private DragonAnimation lyingDeadAnimation = new LyingDeadAnimation();

	private DragonPose poseWingsUp;

	private DragonPose poseWingsMidway;

	private DragonPose poseWingsDown;

	private DragonPose poseFallingDead;

	private DragonPose poseLyingDead;

	private SpriteLook projectile1Look;

	private SpriteLook projectile2Look;

	private SpriteLook projectile3Look;

	private SpriteLook projectile4Look;

	private SpriteImage wingsUpImage;

	private SpriteImage wingsMidwayImage;

	private SpriteImage wingsDownImage;

	private SpriteImage projectile1Image;

	private SpriteImage projectile2Image;

	private SpriteImage projectile3Image;

	private SpriteImage projectile4Image;

	private SpriteImage emblemImage;

	public DragonCatalog() {
	}

	public DragonAnimation getWingedAnimation() {
		return wingedAnimation;
	}

	public DragonAnimation getAscendingWingedAnimation() {
		return ascendingWingedAnimation;
	}

	public DragonAnimation getFreeFallAnimation() {
		return freeFallAnimation;
	}

	public DragonAnimation getFloatingAnimation() {
		return floatingAnimation;
	}

	public DragonAnimation getFallingDeadAnimation() {
		return fallingDeadAnimation;
	}

	public DragonAnimation getLyingDeadAnimation() {
		return lyingDeadAnimation;
	}

	public DragonAnimation createDeadAnimation(long durationMillis) {
		return new DeadAnimation(durationMillis);
	}

	public DragonAnimation createFlyingAnimation(int distanceX, int distanceY, float velocity) {
		return new FlyingAnimation(distanceX, distanceY, velocity);
	}

	public DragonAnimation createShiftAnimation(long durationMillis) {
		return new ShiftAnimation(durationMillis);
	}

	public DragonAnimation createShiftAnimation(long durationMillis, int distanceX) {
		return new ShiftAnimation(durationMillis, distanceX);
	}

	public DragonAnimation createRelocateAnimation(long durationMillis, int distanceX, int distanceY) {
		return new RelocateAnimation(durationMillis, distanceX, distanceY);
	}

	public ProjectileAnimation createProjectileAnimation(int apexDistanceX, int apexDistanceY, int bottomDistanceY) {
		return new ProjectileAnimation(apexDistanceX, apexDistanceY, bottomDistanceY);
	}

	public DragonPose getPoseWingsUp() {
		if (poseWingsUp == null) {
			poseWingsUp = new DragonPose(getWingsUpImage(), 11, 13, 0);
		}
		return poseWingsUp;
	}

	public DragonPose getPoseWingsMidway() {
		if (poseWingsMidway == null) {
			poseWingsMidway = new DragonPose(getWingsMidwayImage(), 12, 5, 3);
		}
		return poseWingsMidway;
	}

	public DragonPose getPoseWingsDown() {
		if (poseWingsDown == null) {
			poseWingsDown = new DragonPose(getWingsDownImage(), 0, 0, 15);
		}
		return poseWingsDown;
	}

	public DragonPose getPoseFallingDead() {
		if (poseFallingDead == null) {
			poseFallingDead = new DragonPose(getWingsUpImage(), 11, 13, 0, -1);
		}
		return poseFallingDead;
	}

	public DragonPose getPoseLyingDead() {
		if (poseLyingDead == null) {
			poseLyingDead = new DragonPose(getWingsDownImage(), 0, 0, 15, -1);
		}
		return poseLyingDead;
	}

	public SpriteLook getProjectile1Look() {
		if (projectile1Look == null) {
			projectile1Look = new SpriteLook(getProjectile1Image(), -2, -2);
		}
		return projectile1Look;
	}

	public SpriteLook getProjectile2Look() {
		if (projectile2Look == null) {
			projectile2Look = new SpriteLook(getProjectile2Image(), -2, -2);
		}
		return projectile2Look;
	}

	public SpriteLook getProjectile3Look() {
		if (projectile3Look == null) {
			projectile3Look = new SpriteLook(getProjectile3Image(), -2, -2);
		}
		return projectile3Look;
	}

	public SpriteLook getProjectile4Look() {
		if (projectile4Look == null) {
			projectile4Look = new SpriteLook(getProjectile4Image(), -2, -2);
		}
		return projectile4Look;
	}

	public SpriteImage getWingsUpImage() {
		if (wingsUpImage == null) {
			wingsUpImage = new SpriteImageRLE(23, 31, new int[] { -1, 9, 0, 1, -2, -1, 9, 0, 1, -1, 11, 0, 2, -2, -1, 9,
					0, 1, -1, 11, 0, 1, -2, -1, 9, 0, 1, -1, 9, 0, 3, -2, -1, 9, 0, 1, -1, 9, 0, 2, -2, -1, 9, 0, 1, -1,
					8, 0, 3, -2, -1, 9, 0, 2, -1, 7, 0, 3, -2, -1, 9, 0, 2, -1, 7, 0, 2, -2, -1, 9, 0, 2, -1, 7, 0, 2,
					-2, -1, 9, 0, 2, -1, 6, 0, 3, -2, -1, 9, 0, 3, -1, 5, 0, 3, -2, -1, 9, 0, 4, -1, 4, 0, 3, -2, -1, 9,
					0, 4, -1, 3, 0, 3, -2, -1, 9, 0, 4, -1, 3, 0, 3, -2, -1, 9, 0, 4, -1, 3, 0, 3, -2, -1, 10, 0, 3, -1,
					3, 0, 3, -2, -1, 10, 0, 3, -1, 3, 0, 2, -2, -1, 10, 0, 4, -1, 1, 0, 3, -2, -1, 11, 0, 3, -1, 1, 0,
					3, -2, -1, 12, 0, 2, -1, 1, 0, 2, -2, -1, 12, 0, 5, -2, -1, 7, 0, 10, -2, -1, 4, 0, 14, -2, -1, 1,
					0, 7, 1, 2, 0, 3, 1, 2, 0, 3, -2, 0, 1, 2, 1, 0, 2, 1, 1, -1, 4, 1, 5, -1, 1, 1, 2, 0, 2, -2, 0, 4,
					-1, 6, 1, 3, -1, 4, 0, 2, -2, 0, 1, -1, 1, 0, 2, -1, 14, 0, 2, -2, -1, 2, 0, 1, -1, 16, 0, 1, -2,
					-1, 19, 0, 1, -2, -1, 19, 0, 2, -1, 1, 0, 1, -2, -1, 20, 0, 3 });
		}
		return wingsUpImage;
	}

	public SpriteImage getWingsMidwayImage() {
		if (wingsMidwayImage == null) {
			wingsMidwayImage = new SpriteImageRLE(30, 31, new int[] { -1, 4, 0, 1, -2, -1, 4, 0, 1, -2, -1, 3, 0, 2, -2,
					-1, 3, 0, 2, -2, -1, 3, 0, 2, -1, 24, 0, 1, -2, -1, 3, 0, 2, -1, 22, 0, 3, -2, -1, 4, 0, 2, -1, 19,
					0, 3, -2, -1, 4, 0, 2, -1, 18, 0, 3, -2, -1, 4, 0, 3, -1, 15, 0, 4, -2, -1, 4, 0, 3, -1, 14, 0, 3,
					-2, -1, 5, 0, 2, -1, 13, 0, 3, -2, -1, 5, 0, 3, -1, 10, 0, 5, -2, -1, 6, 0, 3, -1, 7, 0, 6, -2, -1,
					6, 0, 3, -1, 7, 0, 5, -2, -1, 6, 0, 4, -1, 5, 0, 5, -2, -1, 6, 0, 5, -1, 2, 0, 7, -2, -1, 8, 0, 3,
					-1, 2, 0, 4, -2, -1, 9, 0, 6, -2, -1, 7, 0, 10, -2, -1, 4, 0, 14, -2, -1, 1, 0, 6, 1, 3, 0, 4, 1, 2,
					0, 2, -2, 0, 1, 2, 1, 0, 2, 1, 1, -1, 4, 1, 6, -1, 1, 1, 1, 0, 2, -2, 0, 4, -1, 6, 1, 3, -1, 4, 0,
					2, -2, 0, 1, -1, 1, 0, 2, -1, 14, 0, 2, -2, -1, 2, 0, 1, -1, 15, 0, 2, -2, -1, 16, 0, 3, -2, -1, 15,
					0, 2, -2, -1, 15, 0, 1, -2, -1, 15, 0, 1, -2, -1, 15, 0, 2, -2, -1, 16, 0, 3 });
		}
		return wingsMidwayImage;
	}

	public SpriteImage getWingsDownImage() {
		if (wingsDownImage == null) {
			wingsDownImage = new SpriteImageRLE(47, 19,
					new int[] { -1, 5, 0, 7, -2, -1, 2, 0, 14, -2, 0, 3, -1, 4, 0, 12, -2, -1, 10, 0, 10, -2, -1, 13, 0,
							9, -2, -1, 17, 0, 8, -1, 3, 0, 7, -2, -1, 20, 0, 19, -2, -1, 15, 0, 27, -2, -1, 12, 0, 6, 1,
							3, 0, 4, 1, 2, 0, 2, -1, 2, 0, 13, -2, -1, 11, 0, 1, 2, 1, 0, 2, 1, 1, -1, 4, 1, 6, -1, 1,
							1, 1, 0, 2, -1, 7, 0, 8, -2, -1, 11, 0, 4, -1, 6, 1, 3, -1, 4, 0, 2, -1, 12, 0, 4, -2, -1,
							11, 0, 1, -1, 1, 0, 2, -1, 14, 0, 2, -1, 14, 0, 2, -2, -1, 13, 0, 1, -1, 15, 0, 2, -1, 15,
							0, 1, -2, -1, 30, 0, 2, -2, -1, 30, 0, 2, -2, -1, 31, 0, 1, -2, -1, 31, 0, 1, -1, 5, 0, 1,
							-2, -1, 31, 0, 2, -1, 3, 0, 1, -2, -1, 32, 0, 4 });
		}
		return wingsDownImage;
	}

	public SpriteImage getProjectile1Image() {
		if (projectile1Image == null) {
			projectile1Image = new SpriteImageRLE(4, 4,
					new int[] { -1, 1, 1, 1, 0, 2, -2, 0, 3, 2, 1, -2, 0, 2, 3, 1, 0, 1, -2, -1, 1, 0, 2 });
		}
		return projectile1Image;
	}

	public SpriteImage getProjectile2Image() {
		if (projectile2Image == null) {
			projectile2Image = new SpriteImageRLE(4, 4,
					new int[] { 2, 1, 0, 2, -2, 0, 2, 1, 1, 0, 1, -2, 0, 4, -2, -1, 1, 3, 1, 0, 1 });
		}
		return projectile2Image;
	}

	public SpriteImage getProjectile3Image() {
		if (projectile3Image == null) {
			projectile3Image = new SpriteImageRLE(4, 4,
					new int[] { -1, 1, 0, 2, -2, 0, 1, 1, 1, 0, 2, -2, 0, 1, 2, 1, 3, 1, 0, 1, -2, 0, 3 });
		}
		return projectile3Image;
	}

	public SpriteImage getProjectile4Image() {
		if (projectile4Image == null) {
			projectile4Image = new SpriteImageRLE(4, 4,
					new int[] { -1, 1, 0, 2, -2, 0, 1, 2, 1, 0, 1, 3, 1, -2, 0, 4, -2, -1, 1, 0, 1, 1, 1, 0, 1 });
		}
		return projectile4Image;
	}

	public SpriteImage getEmblemImage() {
		if (emblemImage == null) {
			emblemImage = new SpriteImageRLE(8, 16,
					new int[] { -1, 6, 0, 1, -2, -1, 6, 0, 1, -2, -1, 3, 0, 1, -1, 1, 0, 2, -2, -1, 1, 0, 3, -1, 1, 0,
							2, -2, 0, 4, -1, 1, 0, 2, -2, -1, 2, 0, 1, -1, 1, 0, 3, -2, -1, 1, 0, 2, -1, 1, 0, 4, -2,
							-1, 1, 0, 2, -1, 1, 0, 3, -2, 0, 2, -1, 1, 0, 1, -1, 1, 0, 2, -2, 0, 2, -1, 1, 0, 2, -2, 0,
							3, -1, 2, 0, 1, -2, -1, 1, 0, 4, -1, 1, 0, 1, -2, -1, 2, 0, 3, -1, 1, 0, 1, -2, -1, 5, 0, 2,
							-2, -1, 6, 0, 1, -2, -1, 5, 0, 1 });
		}
		return emblemImage;
	}

	private class DeadAnimation extends DragonAnimation {

		private long durationMillis;

		public DeadAnimation(long durationMillis) {
			this.durationMillis = durationMillis;
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			if (unitTime < 1f) {
				dragon.getColorMap().changeTransparencyFactor(unitTime);
			} else {
				dragon.getColorMap().changeTransparencyFactor(0f); // restore
			}
		}

		@Override
		public long getDurationMillis() {
			return durationMillis;
		}

	}

	private class WingedAnimation extends DragonAnimation {

		public WingedAnimation() {
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			float dy = getDeviation() * (float) Math.sin(-unitTime * 2.0 * Math.PI);
			if (unitTime > 0.5f)
				dy -= getAscent() * (unitTime * 2f - 1f);
			move(dragon, 0, Math.round(dy));
			if (unitTime <= 0.125f) {
				changePose(dragon, getPoseWingsDown());
			} else if (unitTime <= 0.375f) {
				changePose(dragon, getPoseWingsMidway());
			} else if (unitTime <= 0.625f) {
				changePose(dragon, getPoseWingsUp());
			} else if (unitTime <= 0.875f) {
				changePose(dragon, getPoseWingsMidway());
			} else {
				changePose(dragon, getPoseWingsDown());
			}
		}

		protected float getDeviation() {
			return 4f;
		}

		protected int getAscent() {
			return 0;
		}

		@Override
		public long getDurationMillis() {
			return 300L;
		}

		@Override
		public int getVerticalDisplacement() {
			return -getAscent();
		}

	}

	private class AscendingWingedAnimation extends WingedAnimation {

		public AscendingWingedAnimation() {
		}

		@Override
		protected int getAscent() {
			return 16;
		}

		@Override
		public long getDurationMillis() {
			return 450L;
		}

	}

	private class FreeFallAnimation extends DragonAnimation {

		public FreeFallAnimation() {
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			move(dragon, 0, Math.round(unitTime * getDescent()));
		}

		protected int getDescent() {
			return 16;
		}

		@Override
		public long getDurationMillis() {
			return 350L;
		}

		@Override
		public int getVerticalDisplacement() {
			return getDescent();
		}

	}

	private class FloatingAnimation extends DragonAnimation {

		public FloatingAnimation() {
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			move(dragon, 0, Math.round((float) Math.sin(unitTime * 2.0 * Math.PI)));
			changePose(dragon, getPoseWingsDown());
		}

		@Override
		public long getDurationMillis() {
			return 350L;
		}

	}

	private class FallingDeadAnimation extends DragonAnimation {

		public FallingDeadAnimation() {
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			move(dragon, 0, Math.round(unitTime * getDescent()));
			changePose(dragon, getPoseFallingDead());
		}

		protected int getDescent() {
			return 16;
		}

		@Override
		public long getDurationMillis() {
			return 200L;
		}

		@Override
		public int getVerticalDisplacement() {
			return getDescent();
		}

	}

	private class LyingDeadAnimation extends DragonAnimation {

		public LyingDeadAnimation() {
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			changePose(dragon, getPoseLyingDead());
		}

		@Override
		public long getDurationMillis() {
			return 200L;
		}

	}

	private class RelocateAnimation extends DragonAnimation {

		private long durationMillis;

		private int distanceX;

		private int distanceY;

		public RelocateAnimation(long durationMillis, int distanceX, int distanceY) {
			this.durationMillis = durationMillis;
			this.distanceX = distanceX;
			this.distanceY = distanceY;
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			move(dragon, getDistanceX(), getDistanceY());
		}

		@Override
		public long getDurationMillis() {
			return durationMillis;
		}

		@Override
		public int getHorizontalDisplacement() {
			return getDistanceX();
		}

		@Override
		public int getVerticalDisplacement() {
			return getDistanceY();
		}

		public int getDistanceX() {
			return distanceX;
		}

		public int getDistanceY() {
			return distanceY;
		}

	}

	private abstract class DeviationAnimation extends DragonAnimation {

		private PerpetualApproximatingFunction2D deviationFunction;

		protected DeviationAnimation() {
			this.deviationFunction = PerpetualApproximatingFunction2D
					.createQuadraticApproximatingFunction(new ControlValueGenerator() {

						@Override
						public double generateControlValue() {
							return Math.random() * 2.0 - 1.0;
						}
					});
		}

		protected float getDeviationOverTime(float unitTime, long animationDurationMillis) {
			float r = (float) getDeviationFunction().evaluate(animationDurationMillis * unitTime / 250.0);
			return r * getMaximumDeviationOverTime(unitTime);
		}

		protected float getMaximumDeviationOverTime(float unitTime) {
			float r = 1f;
			if (unitTime < 0.25f) {
				r = unitTime / 0.25f;
			} else if (unitTime > 0.75f) {
				r = (1f - unitTime) / 0.25f;
			}
			return r * r * getMaximumDeviation();
		}

		protected float getMaximumDeviation() {
			return 4f;
		}

		private PerpetualApproximatingFunction2D getDeviationFunction() {
			return deviationFunction;
		}

	}

	private class FlyingAnimation extends DeviationAnimation {

		private int distanceX;

		private int distanceY;

		private float velocity; // pixels per second

		private float previousTimeMillis;

		private float positionX;

		private float positionY;

		public FlyingAnimation(int distanceX, int distanceY, float velocity) {
			if (distanceX == 0)
				throw new IllegalArgumentException("Horizontal distance must be <> 0 (" + distanceX + ")");
			this.distanceX = distanceX;
			this.distanceY = distanceY;
			this.velocity = velocity;
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			float t = unitTime * animationDurationMillis;
			float dt = (t - getPreviousTimeMillis()) / 1000f;
			if (dt > 0f) {
				float devx = 0f, devy = 0f;
				float dev = getDeviationOverTime(unitTime, animationDurationMillis);
				if (Math.abs(getDistanceX()) >= Math.abs(getDistanceY())) {
					devy = dev;
				} else {
					devx = dev;
				}
				float d = Math.abs(getDistanceY() / (float) getDistanceX());
				float v = getVelocityOverTime(unitTime, animationDurationMillis);
				float vx = (float) Math.sqrt(v * v / (1f + d * d));
				float vy = d * vx;
				float dx = vx * dt * Math.signum(getDistanceX());
				float dy = vy * dt * Math.signum(getDistanceY());
				setPositionX(getPositionX() + dx);
				setPositionY(getPositionY() + dy);
				move(dragon, Math.round(getPositionX() + devx), Math.round(getPositionY() + devy));
				setPreviousTimeMillis(t);
			}
		}

		protected float getVelocityOverTime(float unitTime, long animationDurationMillis) {
			float t0 = Math.min(getRampTimeMillis() / (float) animationDurationMillis, 0.4f);
			float v = getVelocity() / (1f - t0);
			if (unitTime >= t0 && unitTime <= 1f - t0) {
				return v;
			} else {
				float x = unitTime < 0.5f ? unitTime : 1f - unitTime;
				if (x <= t0 / 2f) {
					return (2f * x / t0) * (2f * x / t0) * v / 2f;
				} else {
					return v - 2f * v * (x / t0 - 1f) * (x / t0 - 1f);
				}
			}
		}

		protected long getRampTimeMillis() {
			return 1000L;
		}

		@Override
		public long getDurationMillis() {
			return Math.round(getDistance() / getVelocity() * 1000f);
		}

		@Override
		public int getHorizontalDisplacement() {
			return getDistanceX();
		}

		@Override
		public int getVerticalDisplacement() {
			return getDistanceY();
		}

		public float getDistance() {
			return (float) Math.sqrt(getDistanceX() * getDistanceX() + getDistanceY() * getDistanceY());
		}

		public int getDistanceX() {
			return distanceX;
		}

		public int getDistanceY() {
			return distanceY;
		}

		public float getVelocity() {
			return velocity;
		}

		private float getPreviousTimeMillis() {
			return previousTimeMillis;
		}

		private void setPreviousTimeMillis(float timeMillis) {
			this.previousTimeMillis = timeMillis;
		}

		private float getPositionX() {
			return positionX;
		}

		private void setPositionX(float x) {
			this.positionX = x;
		}

		private float getPositionY() {
			return positionY;
		}

		private void setPositionY(float y) {
			this.positionY = y;
		}

	}

	private class ShiftAnimation extends DeviationAnimation {

		private int distanceX;

		private float velocity; // pixels per second

		private long durationMillis;

		public ShiftAnimation(long durationMillis) {
			this(durationMillis, 0);
		}

		public ShiftAnimation(long durationMillis, int distanceX) {
			this.durationMillis = durationMillis;
			this.distanceX = distanceX;
			this.velocity = Math.abs(distanceX) / (durationMillis / 1000f);
		}

		@Override
		protected void animateDragon(AnimatedDragon dragon, float unitTime, long animationDurationMillis) {
			float t = unitTime * animationDurationMillis / 1000f;
			float dx = getVelocity() * t * Math.signum(getDistanceX());
			float devx = getDeviationOverTime(unitTime, animationDurationMillis);
			move(dragon, Math.round(dx + devx), 0);
		}

		@Override
		protected float getMaximumDeviation() {
			return 6f;
		}

		@Override
		public long getDurationMillis() {
			return durationMillis;
		}

		@Override
		public int getHorizontalDisplacement() {
			return getDistanceX();
		}

		public int getDistanceX() {
			return distanceX;
		}

		public float getVelocity() {
			return velocity;
		}

	}

	public class ProjectileAnimation extends SpriteAnimation {

		private float a;

		private float b;

		private float c;

		private float hitBaselineT;

		private float length = -1f;

		private float lastUnitTime;

		private long durationMillis;

		private ProjectileAnimation(int apexDistanceX, int apexDistanceY, int bottomDistanceY) {
			float apexT = 20f * (float) Math.sqrt(apexDistanceX * apexDistanceX + apexDistanceY * apexDistanceY);
			this.a = apexDistanceX / apexT;
			this.b = apexDistanceY / apexT * 2f;
			this.c = apexDistanceY / apexT / apexT * 2f;
			this.hitBaselineT = 2f * apexT;
			this.durationMillis = Math
					.round(apexT * (1.0 + Math.sqrt(apexDistanceY * apexDistanceY - apexDistanceY * bottomDistanceY)
							/ Math.abs(apexDistanceY)));
		}

		@Override
		public void animate(AnimatedSprite sprite, float unitTime, long animationDurationMillis) {
			Point2D point = sampleTrajectory(unitTime);
			int x = (int) Math.round(point.getX());
			int y = (int) Math.round(point.getY());
			move(sprite, x, y);
			changeLook(sprite, sampleSpriteLook(unitTime));
			fadeOutBelowBaseline((Projectile) sprite, unitTime);
			setLastUnitTime(unitTime);
		}

		public Point2D sampleTrajectory(float unitTime) {
			float t = unitTime * getDurationMillis();
			float x = getA() * t;
			float y = getB() * t - getC() / 2f * t * t;
			return new Point2D(x, y);
		}

		public Vector2D sampleOrthogonalUnitVector(float unitTime) {
			float t = unitTime * getDurationMillis();
			return new Vector2D(getA(), getB() - t * getC()).getOrthogonalVector().getUnitVector();
		}

		private SpriteLook sampleSpriteLook(float unitTime) {
			int i = Math.round(unitTime * getTrajectoryLength() / 4f) % 4;
			if (getA() > 0f)
				i = 3 - i;
			if (i == 0) {
				return getProjectile1Look();
			} else if (i == 1) {
				return getProjectile2Look();
			} else if (i == 2) {
				return getProjectile3Look();
			} else {
				return getProjectile4Look();
			}
		}

		private void fadeOutBelowBaseline(Projectile projectile, float unitTime) {
			float dur = getDurationMillis();
			float t = unitTime * dur;
			float tb = getHitBaselineT();
			if (t > tb && dur > tb) {
				float r = (t - tb) / (dur - tb);
				projectile.getColorMap().changeTransparencyFactor(r);
			}
		}

		public float getTrajectoryLength() {
			if (length < 0f) {
				length = 0f;
				Point2D p0 = sampleTrajectory(0f);
				for (int i = 1; i <= 30; i++) {
					Point2D p1 = sampleTrajectory(i / 30f);
					length += p0.distanceTo(p1);
					p0 = p1;
				}
			}
			return length;
		}

		private float getA() {
			return a;
		}

		private float getB() {
			return b;
		}

		private float getC() {
			return c;
		}

		private float getHitBaselineT() {
			return hitBaselineT;
		}

		public float getLastUnitTime() {
			return lastUnitTime;
		}

		private void setLastUnitTime(float unitTime) {
			this.lastUnitTime = unitTime;
		}

		public long getDurationMillis() {
			return durationMillis;
		}

	}

}