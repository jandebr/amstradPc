package org.maia.amstrad.gui.browser.carousel.animation.startup.dragon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.AmstradSymbolRenderer;
import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.carousel.animation.startup.CarouselPortholePixelatedAnimation;
import org.maia.amstrad.gui.browser.carousel.animation.startup.dragon.DragonCatalog.ProjectileAnimation;
import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapAlphaComposite;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.gui.sprite.animation.AnimatedSprite;
import org.maia.amstrad.gui.sprite.animation.AnimatedSpriteAdapter;
import org.maia.amstrad.gui.sprite.animation.SpriteAnimation;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D;
import org.maia.graphics2d.function.PerpetualApproximatingFunction2D.ControlValueGenerator;
import org.maia.graphics2d.geometry.Point2D;
import org.maia.graphics2d.geometry.Vector2D;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public class CarouselDragonFightAnimation extends CarouselPortholePixelatedAnimation {

	private DragonCatalog catalog;

	private Castle castle;

	private FearsomeDragon dragon;

	private List<Projectile> projectiles;

	private Emblem emblem;

	private static int WRAPPED_HIT_COUNT = 100;

	public CarouselDragonFightAnimation(AmstradGraphicsContext graphicsContext) {
		super(graphicsContext);
		this.catalog = new DragonCatalog();
		this.projectiles = new Vector<Projectile>();
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setEmblem(new Emblem());
		initCastle();
		initDragon();
	}

	protected void initCastle() {
		setCastle(new Castle());
	}

	protected void initDragon() {
		setDragon(new FearsomeDragon());
		getDragon().start();
	}

	protected void initProjectile() {
		Projectile projectile = new Projectile(createProjectileColors());
		getProjectiles().add(projectile);
		projectile.addSpriteListener(new AnimatedSpriteAdapter() {

			@Override
			public void animationEnded(AnimatedSprite sprite, SpriteAnimation animation) {
				if (!sprite.hasQueuedAnimations()) {
					getProjectiles().remove(projectile);
				}
			}
		});
		projectile.start();
	}

	protected SpriteColorMap createDragonColors() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, new Color(0, 0, 0));
		colorMap.setColor(1, new Color(40, 40, 40));
		colorMap.setColor(2, new Color(222, 13, 13));
		return toMonitorColors(colorMap);
	}

	protected SpriteColorMap createProjectileColors() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, new Color(33, 32, 32));
		colorMap.setColor(1, new Color(85, 84, 77));
		colorMap.setColor(2, new Color(249, 221, 77));
		colorMap.setColor(3, new Color(242, 24, 3));
		return toMonitorColors(colorMap);
	}

	@Override
	protected Panorama createPanorama() {
		Landscape landscape = new Landscape(
				toMonitorColors(UIResources.loadImage("animations/dragon/dragon-landscape-480x370.png")), 1f, 1f);
		return new Panorama(Color.BLACK, null, landscape);
	}

	@Override
	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		launchProjectiles(elapsedTimeMillis);
		super.renderInPorthole(g, elapsedTimeMillis);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.scale(getPixelSize(), getPixelSize());
		renderCastle(g2, elapsedTimeMillis, getCastle());
		renderDragon(g2, getDragon());
		renderProjectiles(g2);
		checkProjectilesHitDragon(getDragon());
		renderHitCount(g2);
		g2.dispose();
	}

	private void launchProjectiles(long elapsedTimeMillis) {
		if (!getDragon().isHit()) {
			double combatFactor = getCastle().getCombatFactor(elapsedTimeMillis);
			double dist = toPoint2D(getDragon().getCenterLocation()).distanceTo(toPoint2D(getCastle().getCenter()));
			double distRel = dist / (0.8 * getPortholePixelWidth());
			double distFactor = Math.pow(3.0, -20.0 * Math.pow(distRel - 0.5, 2.0));
			int n = (int) Math.round(4 * Math.pow(distFactor * combatFactor, 2.0));
			if (getProjectiles().size() < n) {
				initProjectile();
			}
		}
	}

	private void checkProjectilesHitDragon(FearsomeDragon dragon) {
		for (Projectile projectile : getProjectiles()) {
			if (projectile.hits(dragon) && !dragon.isHit()) {
				dragon.hit();
				getCastle().incrementHitCount();
			}
		}
	}

	protected void renderCastle(Graphics2D g, long elapsedTimeMillis, Castle castle) {
		castle.draw(g, elapsedTimeMillis);
	}

	protected void renderDragon(Graphics2D g, FearsomeDragon dragon) {
		dragon.drawUpdated(g);
	}

	protected void renderProjectiles(Graphics2D g) {
		List<Projectile> projectiles = new Vector<Projectile>(getProjectiles());
		for (Projectile projectile : projectiles) {
			renderProjectile(g, projectile);
		}
	}

	protected void renderProjectile(Graphics2D g, Projectile projectile) {
		projectile.drawUpdated(g);
	}

	protected void renderHitCount(Graphics2D g) {
		int hy = 14;
		int hits = getCastle().getHitCount();
		String hitsStr = String.valueOf(hits);
		int pw = getPortholePixelWidth();
		int hw = Math.max(hitsStr.length(), 2) * 8;
		g.setColor(toMonitorColor(new Color(0, 0, 0, 40)));
		g.fillOval((pw - hw) / 2 - 30, -hy - 16, hw + 60, 2 * (hy + 18));
		Emblem emblem = getEmblem().toLeftFacing();
		emblem.move(1 + (pw - hw) / 2 - 12, hy - 3);
		emblem.toShadowLook().draw(g);
		emblem.translate(-1, -1);
		emblem.toStandardLook().draw(g);
		emblem.toRightFacing();
		emblem.move(1 + (pw + hw) / 2 + 4, hy - 3);
		emblem.toShadowLook().draw(g);
		emblem.translate(-1, -1);
		emblem.toStandardLook().draw(g);
		float r = Math.min((System.currentTimeMillis() - getCastle().getLastHitTimeMillis() - 2000L) / 1000f, 1f);
		if (r < 0f) {
			String prevHitsStr = String.valueOf(hits - 1);
			drawInSymbols(g, prevHitsStr, (pw - prevHitsStr.length() * 8) / 2, hy);
		} else if (r < 1f) {
			int ry = Math.round(r * 8f);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.clipRect(0, hy, pw, 8);
			String prevHitsStr = String.valueOf(hits - 1);
			drawInSymbols(g2, prevHitsStr, (pw - prevHitsStr.length() * 8) / 2, hy - ry);
			drawInSymbols(g2, hitsStr, (pw - hitsStr.length() * 8) / 2, hy + 8 - ry);
			g2.dispose();
		} else {
			drawInSymbols(g, hitsStr, (pw - hitsStr.length() * 8) / 2, hy);
		}
	}

	protected void drawInSymbols(Graphics2D g, String str, int x, int y) {
		AmstradSymbolRenderer renderer = getSymbolRenderer(g);
		renderer.color(toMonitorColor(Color.BLACK));
		renderer.drawStr(str, 1 + x, 1 + y);
		renderer.color(toMonitorColor(Color.WHITE));
		renderer.drawStr(str, x, y);
	}

	@Override
	protected int getTargetPixelWidth() {
		return 200;
	}

	private static Point2D toPoint2D(Point point) {
		return new Point2D(point.getX(), point.getY());
	}

	private DragonCatalog getCatalog() {
		return catalog;
	}

	protected Castle getCastle() {
		return castle;
	}

	private void setCastle(Castle castle) {
		this.castle = castle;
	}

	protected FearsomeDragon getDragon() {
		return dragon;
	}

	private void setDragon(FearsomeDragon dragon) {
		this.dragon = dragon;
	}

	protected List<Projectile> getProjectiles() {
		return projectiles;
	}

	protected Emblem getEmblem() {
		return emblem;
	}

	private void setEmblem(Emblem emblem) {
		this.emblem = emblem;
	}

	protected class FearsomeDragon extends Dragon {

		public FearsomeDragon() {
			super(createDragonColors(), getCatalog().getPoseWingsDown().getLookRightFacing(), 0, 0);
		}

		public void start() {
			appendLocalAnimation(getCatalog().getWingedAnimation());
			if (getRandomizer().drawBoolean()) {
				relocateToHoverZone();
			} else {
				relocate();
			}
		}

		public void hit() {
			if (!isHit()) {
				setState(DragonState.FALLING_DEAD);
				clearAnimations();
				DragonAnimation fall = getCatalog().getFallingDeadAnimation();
				int targetY = (int) Math.round(getCastle().getZone().getMaxY())
						+ getRandomizer().drawIntegerNumber(10, 30) / getPixelSize();
				int distanceY = targetY - getY();
				int n = 1;
				if (distanceY > 0)
					n = Math.floorDiv(distanceY, fall.getVerticalDisplacement());
				long durationMillis = n * fall.getDurationMillis();
				appendGlobalAnimation(getCatalog().createShiftAnimation(durationMillis));
				appendLocalAnimation(fall);
			}
		}

		@Override
		protected void nextGlobalAnimation() {
			if (DragonState.RELOCATING.equals(getState())) {
				approachHoverZone();
			} else if (DragonState.APPROACHING.equals(getState())) {
				hover();
			} else if (DragonState.HOVERING.equals(getState())) {
				mount();
			} else if (DragonState.MOUNTING.equals(getState())) {
				if (getRandomizer().drawIntegerNumber(0, 2) == 0) {
					unmount();
				} else {
					retreat();
				}
			} else if (DragonState.UNMOUNTING.equals(getState())) {
				hover();
			} else if (DragonState.RETREATING.equals(getState())) {
				relocate(200L, 500L);
			} else if (DragonState.FALLING_DEAD.equals(getState())) {
				lieDead();
			} else if (DragonState.LYING_DEAD.equals(getState())) {
				resurrect();
			}
		}

		@Override
		protected void nextLocalAnimation() {
			DragonAnimation animation = getCatalog().getWingedAnimation();
			int animationRepeat = 1;
			if (DragonState.APPROACHING.equals(getState()) || DragonState.RETREATING.equals(getState())) {
				double c = Math.abs(Math.cos(new Vector2D(getCurrentGlobalAnimation().getHorizontalDisplacement(),
						getCurrentGlobalAnimation().getVerticalDisplacement()).getAngleInRadians()));
				if (getRandomizer().drawDoubleUnitNumber() < c * 0.4) {
					animation = getCatalog().getFloatingAnimation();
				}
			} else if (DragonState.HOVERING.equals(getState())) {
				int yMin = Math.round(getCastle().getZone().y * 0.6f);
				int yMax = getCastle().getCenter().y - getLook().getImage().getHeight();
				if (getY() < yMin) {
					animation = getCatalog().getFreeFallAnimation();
				} else if (getY() > yMax) {
					animation = getCatalog().getAscendingWingedAnimation();
				} else {
					int rnd = getRandomizer().drawIntegerNumber(0, 3);
					if (rnd == 0) {
						animation = getCatalog().getFreeFallAnimation();
					} else if (rnd == 1) {
						animation = getCatalog().getWingedAnimation();
						animationRepeat = 2;
					} else if (rnd == 2) {
						animation = getCatalog().getAscendingWingedAnimation();
						animationRepeat = 2;
					} else {
						animation = getCatalog().getFloatingAnimation();
					}
				}
			} else if (DragonState.MOUNTING.equals(getState())) {
				animation = getCatalog().getAscendingWingedAnimation();
			} else if (DragonState.UNMOUNTING.equals(getState())) {
				animation = getCatalog().getFreeFallAnimation();
			} else if (DragonState.FALLING_DEAD.equals(getState())) {
				animation = getCatalog().getFallingDeadAnimation();
			} else if (DragonState.LYING_DEAD.equals(getState())) {
				animation = getCatalog().getLyingDeadAnimation();
			}
			appendLocalAnimationRepeating(animation, animationRepeat);
		}

		private void relocateToHoverZone() {
			setState(DragonState.APPROACHING);
			Point2D target = selectHoverTargetLocation();
			int dx = (int) Math.round(target.getX()) - getX();
			int dy = (int) Math.round(target.getY()) - getY();
			appendGlobalAnimation(getCatalog().createRelocateAnimation(100L, dx, dy));
		}

		private void relocate() {
			relocate(0L, 0L);
		}

		private void relocate(long minDurationMillis, long maxDurationMillis) {
			setState(DragonState.RELOCATING);
			long durationMillis = getRandomizer().drawLongIntegerNumber(Math.max(minDurationMillis, 100L),
					Math.max(maxDurationMillis, 100L));
			int dx = (getX() < getCastle().getCenter().x ? -50 : getPortholePixelWidth() + 10) - getX();
			int dy = getRandomizer().drawIntegerNumber(-20, getCastle().getCenter().y / 2) - getY();
			appendGlobalAnimation(getCatalog().createRelocateAnimation(durationMillis, dx, dy));
		}

		private void approachHoverZone() {
			setState(DragonState.APPROACHING);
			Point2D target = selectHoverTargetLocation();
			int distanceX = (int) Math.round(target.getX()) - getX();
			int distanceY = (int) Math.round(target.getY()) - getY();
			turnIf((distanceX > 0 && isLeftFacing()) || (distanceX < 0 && isRightFacing()));
			appendGlobalAnimation(getCatalog().createFlyingAnimation(distanceX, distanceY, getVelocity()));
		}

		private Point2D selectHoverTargetLocation() {
			Point2D target = null;
			Point2D current = new Point2D(getX(), getY());
			int w = getLook().getImage().getWidth();
			int h = getLook().getImage().getHeight();
			int xMin = -getLook().getImageOffsetX();
			int xMax = xMin + getPortholePixelWidth() - w - 20;
			Randomizer rnd = getRandomizer();
			do {
				List<Rectangle> zones = getCastle().getHoverZones();
				Rectangle zone = zones.get(rnd.drawIntegerNumber(0, zones.size() - 1));
				double tx = zone.getX() - w / 2.0 + rnd.drawDoubleUnitNumber() * zone.getWidth();
				if (tx >= xMin && tx <= xMax) {
					double ty = zone.getY() - h / 2.0 + rnd.drawDoubleUnitNumber() * zone.getHeight();
					Point2D tp = new Point2D(tx, ty);
					if (current.distanceTo(tp) >= 40.0) {
						target = tp;
					}
				}
			} while (target == null);
			return target;
		}

		private void hover() {
			setState(DragonState.HOVERING);
			int cx = getCastle().getCenter().x;
			turnIf((getX() > cx && isRightFacing()) || (getX() < cx && isLeftFacing()));
			long durationMillis = getRandomizer().drawLongIntegerNumber(1500L, 2500L);
			appendGlobalAnimation(getCatalog().createShiftAnimation(durationMillis));
		}

		private void mount() {
			setState(DragonState.MOUNTING);
			long wingedDur = getCatalog().getAscendingWingedAnimation().getDurationMillis();
			int wingedY = getCatalog().getAscendingWingedAnimation().getVerticalDisplacement(); // negative
			int distanceY = Math.max(wingedY * getRandomizer().drawIntegerNumber(5, 10), -getY() + 20);
			int n = Math.floorDiv(-distanceY, -wingedY);
			int distanceX = (int) Math.round(n * 8.0 * Math.pow(getRandomizer().drawDoubleUnitNumber(), 0.2));
			if (getRandomizer().drawBoolean())
				distanceX *= -1;
			appendGlobalAnimation(getCatalog().createShiftAnimation(n * wingedDur, distanceX));
		}

		private void unmount() {
			setState(DragonState.UNMOUNTING);
			Point2D target = selectHoverTargetLocation();
			int distanceX = (int) Math.round(target.getX()) - getX();
			int distanceY = (int) Math.round(target.getY()) - getY();
			int wingedY = getCatalog().getFreeFallAnimation().getVerticalDisplacement(); // positive
			long wingedDur = getCatalog().getFreeFallAnimation().getDurationMillis();
			int n = 1;
			if (distanceY > 0)
				n = Math.floorDiv(distanceY, wingedY);
			long durationMillis = Math.max(n * wingedDur, Math.round(Math.abs(distanceX) / getVelocity() * 1000f));
			appendGlobalAnimation(getCatalog().createShiftAnimation(durationMillis, distanceX));
		}

		private void retreat() {
			setState(DragonState.RETREATING);
			Point2D target = selectRetreatTargetLocation();
			int distanceX = (int) Math.round(target.getX()) - getX();
			int distanceY = (int) Math.round(target.getY()) - getY();
			turnIf((distanceX > 0 && isLeftFacing()) || (distanceX < 0 && isRightFacing()));
			appendGlobalAnimation(getCatalog().createFlyingAnimation(distanceX, distanceY, getVelocity()));
		}

		private Point2D selectRetreatTargetLocation() {
			int ty = getRandomizer().drawIntegerNumber(-20, getCastle().getCenter().y / 2);
			int tx = getRandomizer().drawBoolean() ? -50 : getPortholePixelWidth() + 10;
			return new Point2D(tx, ty);
		}

		private void lieDead() {
			setState(DragonState.LYING_DEAD);
			appendGlobalAnimation(getCatalog().createDeadAnimation(2000L));
		}

		private void resurrect() {
			relocate(1000L, 2000L);
		}

		protected float getVelocity() {
			return 60f;
		}

	}

	protected class Projectile extends AnimatedSprite {

		private SpriteColorMapImpl baseColorMap;

		private PerpetualApproximatingFunction2D tailModulationFunction;

		public Projectile(SpriteColorMap baseColorMap) {
			super(new SpriteColorMapAlphaComposite(baseColorMap));
			this.baseColorMap = (SpriteColorMapImpl) baseColorMap;
			this.tailModulationFunction = PerpetualApproximatingFunction2D
					.createQuadraticApproximatingFunction(new ControlValueGenerator() {

						@Override
						public double generateControlValue() {
							return Math.random();
						}
					});
		}

		public void start() {
			Point start = selectStartLocation();
			reset(null, start.x, start.y);
			ProjectileAnimation animation = createAnimation(start);
			appendFreeze(getRandomizer().drawLongIntegerNumber(200L, 1000L));
			appendAnimation(animation, animation.getDurationMillis());
		}

		private Point selectStartLocation() {
			Rectangle zone = getCastle().getZone();
			int x = zone.x + getRandomizer().drawIntegerNumber(0, zone.width - 1);
			int y = zone.y + getRandomizer().drawIntegerNumber(0, zone.height - 1);
			return new Point(x, y);
		}

		private ProjectileAnimation createAnimation(Point start) {
			Randomizer rnd = getRandomizer();
			Point dloc = getDragon().getCenterLocation();
			int ps = getPixelSize();
			int dx = dloc.x - start.x;
			if (dx > 0) {
				dx += rnd.drawIntegerNumber(Math.max(-60 / ps, -dx), Math.min(60 / ps, dx));
			} else if (dx < 0) {
				dx += rnd.drawIntegerNumber(Math.max(-60 / ps, dx), Math.min(60 / ps, -dx));
			}
			if (dx == 0) {
				dx = rnd.drawIntegerNumber(20, 30) / ps * (rnd.drawBoolean() ? 1 : -1);
			} else if (Math.abs(dx) < 20 / ps) {
				dx = (int) Math.signum(dx) * rnd.drawIntegerNumber(20, 30) / ps;
			}
			int dy = Math.min(dloc.y - start.y + rnd.drawIntegerNumber(-30, 30) / ps, -20 / ps);
			int by = rnd.drawIntegerNumber(10, 30) / ps;
			return getCatalog().createProjectileAnimation(dx, dy, by);
		}

		@Override
		public void draw(Graphics2D g) {
			modulateColors();
			if (getCurrentAnimation() instanceof ProjectileAnimation) {
				drawTail((ProjectileAnimation) getCurrentAnimation(), g);
			}
			super.draw(g);
		}

		private void modulateColors() {
			SpriteColorMapImpl baseColorMap = getBaseColorMap();
			int n = baseColorMap.getMaxColorIndex();
			List<Color> baseColors = new Vector<Color>(n);
			for (int i = 1; i <= n; i++)
				baseColors.add(baseColorMap.getColor(i));
			Collections.shuffle(baseColors);
			for (int i = 1; i <= n; i++)
				baseColorMap.setColor(i, baseColors.get(i - 1));
			float tf = getColorMap().getTransparencyFactor();
			getColorMap().changeTransparencyFactor(-1f); // bust cache
			getColorMap().changeTransparencyFactor(tf);
		}

		protected void drawTail(ProjectileAnimation animation, Graphics2D g) {
			float tf = getColorMap().getTransparencyFactor();
			Color c1 = ColorUtils.setTransparency(toMonitorColor(Color.BLACK), tf);
			Color c2 = ColorUtils.setTransparency(toMonitorColor(new Color(85, 84, 77)), tf);
			Randomizer rnd = getRandomizer();
			float t = animation.getLastUnitTime();
			float dt = 1f / animation.getTrajectoryLength();
			float tailmod = (float) getTailModulationFunction().evaluate(t / dt / 5f);
			float dmax = (16f + 20f * tailmod) * (float) Math.pow(Math.min(t, 0.15) / 0.15, 2.0);
			for (int i = 0; i < 24; i++) {
				float d1 = 9f * (float) Math.abs(rnd.drawGaussian());
				if (d1 <= dmax) {
					float d2 = (float) rnd.drawGaussian(0, 2.0 - 1.9 * d1 / dmax);
					if (Math.abs(d2) <= 3f) {
						float ti = t - d1 * dt;
						Point2D pi = animation.sampleTrajectory(ti);
						Vector2D vi = animation.sampleOrthogonalUnitVector(ti);
						vi.scale(d2);
						Point2D pj = pi.plus(vi);
						int x = getAnimationOffsetX() + (int) Math.round(pj.getX());
						int y = getAnimationOffsetY() + (int) Math.round(pj.getY());
						g.setColor(rnd.drawBoolean() ? c1 : c2);
						g.fillRect(x, y, 1, 1);
					}
				}
			}
		}

		public boolean hits(FearsomeDragon dragon) {
			return toPoint2D(getCenterLocation()).distanceTo(toPoint2D(dragon.getCenterLocation())) <= 4.0;
		}

		@Override
		public SpriteColorMapAlphaComposite getColorMap() {
			return (SpriteColorMapAlphaComposite) super.getColorMap();
		}

		private SpriteColorMapImpl getBaseColorMap() {
			return baseColorMap;
		}

		private PerpetualApproximatingFunction2D getTailModulationFunction() {
			return tailModulationFunction;
		}

	}

	protected class Castle {

		private Rectangle zone;

		private List<Rectangle> hoverZones;

		private List<CastleLight> lights;

		private PerpetualApproximatingFunction2D combatFunction;

		private int hitCount;

		private long lastHitTimeMillis;

		public Castle() {
			this.combatFunction = PerpetualApproximatingFunction2D
					.createLinearInterpolatingFunction(new ControlValueGenerator() {

						@Override
						public double generateControlValue() {
							return getRandomizer().drawDoubleUnitNumber();
						}
					});
			this.hitCount = Math.min((int) Math.round(2.0 * Math.abs(getRandomizer().drawGaussian())), 6);
		}

		public void draw(Graphics2D g, long elapsedTimeMillis) {
			for (CastleLight light : getLights()) {
				light.draw(g, elapsedTimeMillis);
			}
		}

		public List<Rectangle> getHoverZones() {
			if (hoverZones == null) {
				hoverZones = new Vector<Rectangle>(2);
				hoverZones.add(projectLandscapeRegionToView(new Rectangle(200, 120, 60, 60)));
				hoverZones.add(projectLandscapeRegionToView(new Rectangle(330, 120, 60, 60)));
			}
			return hoverZones;
		}

		public Point getCenter() {
			Rectangle zone = getZone();
			return new Point((int) Math.round(zone.getCenterX()), (int) Math.round(zone.getCenterY()));
		}

		public Rectangle getZone() {
			if (zone == null) {
				zone = projectLandscapeRegionToView(new Rectangle(279, 221, 37, 25));
			}
			return zone;
		}

		private List<CastleLight> getLights() {
			if (lights == null) {
				lights = new Vector<CastleLight>(7);
				lights.add(
						new CastleLight(projectLandscapeCoordinateToView(new Point(281, 233)), new Color(15, 23, 34)));
				lights.add(
						new CastleLight(projectLandscapeCoordinateToView(new Point(294, 233)), new Color(12, 20, 34)));
				lights.add(
						new CastleLight(projectLandscapeCoordinateToView(new Point(298, 233)), new Color(12, 19, 31)));
				lights.add(
						new CastleLight(projectLandscapeCoordinateToView(new Point(302, 226)), new Color(11, 18, 27)));
				lights.add(
						new CastleLight(projectLandscapeCoordinateToView(new Point(304, 228)), new Color(36, 41, 52)));
				lights.add(
						new CastleLight(projectLandscapeCoordinateToView(new Point(309, 240)), new Color(16, 20, 27)));
				lights.add(
						new CastleLight(projectLandscapeCoordinateToView(new Point(315, 247)), new Color(33, 37, 48)));
			}
			return lights;
		}

		public double getCombatFactor(long elapsedTimeMillis) {
			return getCombatFunction().evaluate(elapsedTimeMillis / 500.0);
		}

		public void incrementHitCount() {
			setHitCount(getHitCount() % (WRAPPED_HIT_COUNT - 1) + 1);
			setLastHitTimeMillis(System.currentTimeMillis());
		}

		private PerpetualApproximatingFunction2D getCombatFunction() {
			return combatFunction;
		}

		public int getHitCount() {
			return hitCount;
		}

		private void setHitCount(int hitCount) {
			this.hitCount = hitCount;
		}

		public long getLastHitTimeMillis() {
			return lastHitTimeMillis;
		}

		private void setLastHitTimeMillis(long timeMillis) {
			this.lastHitTimeMillis = timeMillis;
		}

	}

	private class CastleLight {

		private Point location;

		private Color originalColor;

		private Color lightColor;

		private PerpetualApproximatingFunction2D brightnessFunction;

		public CastleLight(Point location, Color originalColor) {
			this.location = location;
			this.originalColor = originalColor;
			this.lightColor = new Color(239, 242, 46);
			this.brightnessFunction = PerpetualApproximatingFunction2D
					.createLinearInterpolatingFunction(new ControlValueGenerator() {

						@Override
						public double generateControlValue() {
							return 0.2 + 0.8 * getRandomizer().drawDoubleUnitNumber();
						}
					});
		}

		public void draw(Graphics2D g, long elapsedTimeMillis) {
			Point loc = getLocation();
			float r = (float) getBrightnessFunction().evaluate(elapsedTimeMillis / 400.0);
			Color c = toMonitorColor(ColorUtils.interpolate(getOriginalColor(), getLightColor(), r));
			g.setColor(c);
			g.fillRect(loc.x, loc.y, 1, 1);
		}

		private Point getLocation() {
			return location;
		}

		private Color getOriginalColor() {
			return originalColor;
		}

		private Color getLightColor() {
			return lightColor;
		}

		private PerpetualApproximatingFunction2D getBrightnessFunction() {
			return brightnessFunction;
		}

	}

	protected class Emblem extends Sprite {

		public Emblem() {
			super(getCatalog().getEmblemImage(), new SpriteColorMapImpl());
			toStandardLook();
		}

		public Emblem toLeftFacing() {
			if (isMirroredX())
				flipX();
			return this;
		}

		public Emblem toRightFacing() {
			if (!isMirroredX())
				flipX();
			return this;
		}

		public Emblem toStandardLook() {
			((SpriteColorMapImpl) getColorMap()).setDefaultColor(toMonitorColor(Color.WHITE));
			return this;
		}

		public Emblem toShadowLook() {
			((SpriteColorMapImpl) getColorMap()).setDefaultColor(toMonitorColor(Color.BLACK));
			return this;
		}

	}

}