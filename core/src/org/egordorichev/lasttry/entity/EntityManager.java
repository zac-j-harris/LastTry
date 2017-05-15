package org.egordorichev.lasttry.entity;

import com.badlogic.gdx.Gdx;
import org.egordorichev.lasttry.LastTry;
import org.egordorichev.lasttry.entity.enemy.Enemies;
import org.egordorichev.lasttry.entity.enemy.Enemy;
import org.egordorichev.lasttry.item.block.Block;
import org.egordorichev.lasttry.util.Callable;
import org.egordorichev.lasttry.util.Camera;
import org.egordorichev.lasttry.util.Rectangle;
import org.egordorichev.lasttry.util.Util;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EntityManager {
	private List<Entity> entities = new ArrayList<>();
	private List<Enemy> enemyEntities = new ArrayList<>();
	private List<Entity> clearList = new ArrayList<>();
	private static EntityComparator comparator = new EntityComparator();

	public static final int ENEMY_DESPAWN_SWEEP_INTERVAL = 1;
	// private List<Gore> gores = new ArrayList<>(); : TODO

	public EntityManager() {
		Util.runInThread(new Callable() {
			@Override
			public void call() {
				attemptDespawnEnemies();
			}
		}, ENEMY_DESPAWN_SWEEP_INTERVAL);
	}

	public void render() {
		Rectangle camera = new Rectangle(Camera.game.position.x - 16, Camera.game.position.y - 16, Gdx.graphics.getWidth() + 32, Gdx.graphics.getHeight());

		for (Entity entity : this.entities) {
			if (entity.physics.getHitbox().intersects(camera)) {
				entity.render();
			}
		}
	}

	public void update(int dt) {
		for (Entity entity : this.clearList) {
			this.entities.remove(entity);
		}

		this.clearList.clear();

		for (int i = this.entities.size() - 1; i >= 0; i--) {
			Entity entity = this.entities.get(i);
			entity.update(dt);

			if (!entity.isActive()) {
				this.entities.remove(i);
			}
		}
	}

	public Entity spawn(Entity entity, int x, int y) {
		entity.spawn(x, y);
		this.entities.add(entity);
		this.sort();

		return entity;
	}

	private static class EntityComparator implements Comparator<Entity> {
		@Override
		public int compare(Entity o, Entity t1) {
			if (o.getZIndex() > t1.getZIndex()) {
				return 1;
			} else if (o.getZIndex() < t1.getZIndex()) {
				return -1;
			}

			return 0;
		}
	}

	private void sort() {
		Collections.sort(this.entities, comparator);
	}

	public Enemy spawnEnemy(String name, int x, int y) {
		Enemy enemy = Enemies.create(name);

		if (enemy == null) {
			return null;
		}

		this.enemyEntities.add(enemy);
		this.spawn(enemy, x, y);
		return enemy;
	}

	public void remove(Entity entity) {
		this.entities.remove(entity);
	}

	public void markForRemoval(Entity entity) {
		this.clearList.add(entity);

		if (entity instanceof Enemy){
			enemyEntities.remove(entity);
		}
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public List<Enemy> getEnemyEntities() {
		return enemyEntities;
	}

	private synchronized void attemptDespawnEnemies() {
		try {
			for (int i = 0; i < this.enemyEntities.size(); i++){
				CreatureWithAI creatureWithAI = enemyEntities.get(i);

				// Acquire a read only lock, source: http://winterbe.com/posts/2015/04/30/java8-concurrency-tutorial-synchronized-locks-examples/
				ReadWriteLock readOnlyLock = new ReentrantReadWriteLock();

				readOnlyLock.readLock().lock();
				creatureWithAI.tryToDespawn();
				readOnlyLock.readLock().unlock();
			}
		} catch (Exception e) {
			LastTry.handleException(e);
		}
	}
}