package org.maia.amstrad.gui.sprite.animation;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.sprite.Sprite;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.util.GenericListenerList;

public class AnimatedSprite {

	private Sprite target;

	private SpriteLook look;

	private int animationOffsetX;

	private int animationOffsetY;

	private float animationOffsetRotationDegrees;

	private List<TimedSpriteAnimation> animationQueue;

	private TimedSpriteAnimation currentTimedAnimation;

	private GenericListenerList<AnimatedSpriteListener> spriteListeners;

	public AnimatedSprite(SpriteColorMap colorMap) {
		this(new Sprite(colorMap));
	}

	public AnimatedSprite(Sprite target) {
		this.target = target;
		this.animationQueue = new Vector<TimedSpriteAnimation>();
		this.spriteListeners = new GenericListenerList<AnimatedSpriteListener>();
	}

	public void addSpriteListener(AnimatedSpriteListener listener) {
		getSpriteListeners().addListener(listener);
	}

	public void removeSpriteListener(AnimatedSpriteListener listener) {
		getSpriteListeners().removeListener(listener);
	}

	public void reset(SpriteLook look) {
		reset(look, 0, 0);
	}

	public void reset(SpriteLook look, int x, int y) {
		reset(look, x, y, 0f);
	}

	public void reset(SpriteLook look, int x, int y, float rotationDegrees) {
		clearAnimations();
		changeLook(look);
		getTarget().move(x, y);
		getTarget().setRotationDegrees(rotationDegrees);
		setAnimationOffsetX(0);
		setAnimationOffsetY(0);
		setAnimationOffsetRotationDegrees(0f);
	}

	public void clearAnimations() {
		synchronized (getAnimationQueue()) {
			if (getCurrentTimedAnimation() != null) {
				fireAnimationCancelled(getCurrentTimedAnimation().getAnimation());
			}
			getAnimationQueue().clear();
			setCurrentTimedAnimation(null);
		}
	}

	public void clearAnimationsInFuture() {
		synchronized (getAnimationQueue()) {
			long now = System.currentTimeMillis();
			int i = 0;
			while (i < getQueuedAnimations() && getAnimationQueue().get(i).getStartTimeMillis() <= now)
				i++;
			while (i < getQueuedAnimations())
				getAnimationQueue().remove(i);
		}
	}

	public void appendAnimation(SpriteAnimation animation, long animationDurationMillis) {
		synchronized (getAnimationQueue()) {
			TimedSpriteAnimation last = getLastAnimationInQueue();
			long animationStartTimeMillis = last != null ? last.getEndTimeMillis() : System.currentTimeMillis();
			getAnimationQueue()
					.add(new TimedSpriteAnimation(animation, animationStartTimeMillis, animationDurationMillis));
		}
	}

	public void appendAnimationRepeating(SpriteAnimation animation, long animationDurationMillis, int repeats) {
		synchronized (getAnimationQueue()) {
			for (int i = 0; i < repeats; i++) {
				appendAnimation(animation, animationDurationMillis);
			}
		}
	}

	public void appendFreeze(long durationMillis) {
		appendAnimation(new SpriteAnimation() {

			@Override
			public void animate(AnimatedSprite sprite, float unitTime, long animationDurationMillis) {
				// nothing
			}
		}, durationMillis);
	}

	public void update() {
		long now = System.currentTimeMillis();
		synchronized (getAnimationQueue()) {
			TimedSpriteAnimation animation = getFirstAnimationInQueue();
			while (animation != null) {
				if (animation.getStartTimeMillis() > now) {
					// future
					animation = null;
				} else {
					if (!animation.equals(getCurrentTimedAnimation())) {
						setCurrentTimedAnimation(animation);
						setAnimationOffsetX(getTarget().getX());
						setAnimationOffsetY(getTarget().getY());
						setAnimationOffsetRotationDegrees(getTarget().getRotationDegrees());
						fireAnimationStarted(animation.getAnimation());
					}
					if (animation.getEndTimeMillis() <= now || animation.getDurationMillis() == 0L) {
						// passed
						animation.getAnimation().animate(this, 1f, animation.getDurationMillis()); // ensure proper end
																									// state
						getAnimationQueue().remove(0);
						setCurrentTimedAnimation(null);
						fireAnimationEnded(animation.getAnimation());
						animation = getFirstAnimationInQueue();
					} else {
						// current
						float unitTime = (now - animation.getStartTimeMillis()) / (float) animation.getDurationMillis();
						animation.getAnimation().animate(this, unitTime, animation.getDurationMillis());
						animation = null;
					}
				}
			}
		}
	}

	public void draw(Graphics2D g) {
		SpriteLook look = getLook();
		if (look != null) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(look.getImageOffsetX(), look.getImageOffsetY());
			getTarget().draw(g2);
			g2.dispose();
		} else {
			getTarget().draw(g);
		}
	}

	public final void drawUpdated(Graphics2D g) {
		update();
		draw(g);
	}

	protected void changeLook(SpriteLook look) {
		setLook(look);
		if (look != null) {
			getTarget().changeImage(look.getImage());
			if (getTarget().isMirroredX() ^ look.isImageMirroredX())
				getTarget().flipX();
			if (getTarget().isMirroredY() ^ look.isImageMirroredY())
				getTarget().flipY();
		} else {
			getTarget().changeImage(null);
		}
	}

	void moveAnimated(int x, int y) {
		getTarget().move(getAnimationOffsetX() + x, getAnimationOffsetY() + y);
	}

	void rotateAnimated(float rotationDegrees) {
		getTarget().setRotationDegrees(getAnimationOffsetRotationDegrees() + rotationDegrees);
	}

	private void fireAnimationStarted(SpriteAnimation animation) {
		for (AnimatedSpriteListener listener : getSpriteListeners()) {
			listener.animationStarted(this, animation);
		}
	}

	private void fireAnimationEnded(SpriteAnimation animation) {
		for (AnimatedSpriteListener listener : getSpriteListeners()) {
			listener.animationEnded(this, animation);
		}
	}

	private void fireAnimationCancelled(SpriteAnimation animation) {
		for (AnimatedSpriteListener listener : getSpriteListeners()) {
			listener.animationCancelled(this, animation);
		}
	}

	private TimedSpriteAnimation getFirstAnimationInQueue() {
		TimedSpriteAnimation animation = null;
		if (!getAnimationQueue().isEmpty()) {
			animation = getAnimationQueue().get(0);
		}
		return animation;
	}

	private TimedSpriteAnimation getLastAnimationInQueue() {
		TimedSpriteAnimation animation = null;
		if (!getAnimationQueue().isEmpty()) {
			animation = getAnimationQueue().get(getQueuedAnimations() - 1);
		}
		return animation;
	}

	public SpriteAnimation getCurrentAnimation() {
		SpriteAnimation animation = null;
		TimedSpriteAnimation ta = getCurrentTimedAnimation();
		if (ta != null) {
			animation = ta.getAnimation();
		}
		return animation;
	}

	public boolean hasQueuedAnimations() {
		return getQueuedAnimations() > 0;
	}

	public int getQueuedAnimations() {
		return getAnimationQueue().size();
	}

	public SpriteColorMap getColorMap() {
		return getTarget().getColorMap();
	}

	public Point getCenterLocation() {
		int x = getX();
		int y = getY();
		SpriteLook look = getLook();
		if (look != null) {
			x += look.getImageOffsetX() + look.getImage().getWidth() / 2;
			y += look.getImageOffsetY() + look.getImage().getHeight() / 2;
		}
		return new Point(x, y);
	}

	public int getX() {
		return getTarget().getX();
	}

	public int getY() {
		return getTarget().getY();
	}

	protected Sprite getTarget() {
		return target;
	}

	public SpriteLook getLook() {
		return look;
	}

	private void setLook(SpriteLook look) {
		this.look = look;
	}

	protected int getAnimationOffsetX() {
		return animationOffsetX;
	}

	protected void setAnimationOffsetX(int x) {
		this.animationOffsetX = x;
	}

	protected int getAnimationOffsetY() {
		return animationOffsetY;
	}

	protected void setAnimationOffsetY(int y) {
		this.animationOffsetY = y;
	}

	protected float getAnimationOffsetRotationDegrees() {
		return animationOffsetRotationDegrees;
	}

	private void setAnimationOffsetRotationDegrees(float rotationDegrees) {
		this.animationOffsetRotationDegrees = rotationDegrees;
	}

	private List<TimedSpriteAnimation> getAnimationQueue() {
		return animationQueue;
	}

	private TimedSpriteAnimation getCurrentTimedAnimation() {
		return currentTimedAnimation;
	}

	private void setCurrentTimedAnimation(TimedSpriteAnimation animation) {
		this.currentTimedAnimation = animation;
	}

	private GenericListenerList<AnimatedSpriteListener> getSpriteListeners() {
		return spriteListeners;
	}

	private static class TimedSpriteAnimation {

		private SpriteAnimation animation;

		private long startTimeMillis;

		private long durationMillis;

		public TimedSpriteAnimation(SpriteAnimation animation, long startTimeMillis, long durationMillis) {
			this.animation = animation;
			this.startTimeMillis = startTimeMillis;
			this.durationMillis = durationMillis;
		}

		public long getEndTimeMillis() {
			return getStartTimeMillis() + getDurationMillis();
		}

		public long getStartTimeMillis() {
			return startTimeMillis;
		}

		public long getDurationMillis() {
			return durationMillis;
		}

		public SpriteAnimation getAnimation() {
			return animation;
		}

	}

}