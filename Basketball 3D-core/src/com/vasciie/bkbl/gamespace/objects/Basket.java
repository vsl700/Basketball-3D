package com.vasciie.bkbl.gamespace.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.vasciie.bkbl.MyGdxGame;

public abstract class Basket extends GameObject {
	
	private ModelInstance tabCenter;

	private static final float standW = 1;
	private static final float standH = 9;
	private static final float standD = 1;
	private static final float tabW = 5.5f;
	private static final float tabH = 3;
	private static final float tabD = 0.115f;
	private static final float tabCentW = 2.3f;
	private static final float tabCentH = 0.15f;
	private static final float tabCentH1 = 1.2f;
	private static final float tabCentD = 0.01f;
	private static final float bkHoldW = 0.08f;
	private static final float bkHoldD = 0.2f;
	
	protected abstract Color getColor();

	private static final ModelBuilder mb = new ModelBuilder();
	private static final ModelBuilder childMB = new ModelBuilder();
	@Override
	protected void createModels() {
		

		
		Material material = new Material(ColorAttribute.createDiffuse(Color.WHITE));
		Material centerMaterial = new Material(ColorAttribute.createDiffuse(getColor()));
		
		int divisions = 10;
		
		mb.begin();
		childMB.begin();
		
		Node stand = mb.node();
		stand.id = "stand";
		CylinderShapeBuilder.build(mb.part(stand.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), standW, standH, standD, divisions);
		stand.translation.set(0, standH / 2, 0);
		
		Node tab = childMB.node();
		tab.id = "tab";
		stand.addChild(tab);
		BoxShapeBuilder.build(childMB.part(tab.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), tabW, tabH, tabD);
		tab.translation.set(0, standH / 2, -standD / 2 - tabD / 2);
		
		Node basketHold = childMB.node();
		basketHold.id = "basketHold";
		tab.addChild(basketHold);
		BoxShapeBuilder.build(childMB.part(basketHold.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), bkHoldW, bkHoldW, bkHoldD);
		basketHold.translation.set(0, -tabH / 2 + bkHoldW / 2, -tabD / 2 - bkHoldD / 2);
		
		Node basket1 = childMB.node();
		basket1.id = "basket1";
		basketHold.addChild(basket1);
		BoxShapeBuilder.build(childMB.part(basket1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), tabCentW, bkHoldW, bkHoldW);
		basket1.translation.set(0, 0, -bkHoldD / 2 + bkHoldW / 2);
		
		Node basket2 = childMB.node();
		basket2.id = "basket2";
		basket1.addChild(basket2);
		BoxShapeBuilder.build(childMB.part(basket2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), bkHoldW, bkHoldW, tabCentW);
		basket2.translation.set(-tabCentW / 2 + bkHoldW / 2, 0, -tabCentW / 2);
		
		Node basket3 = childMB.node();
		basket3.id = "basket3";
		basket1.addChild(basket3);
		BoxShapeBuilder.build(childMB.part(basket3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), tabCentW, bkHoldW, bkHoldW);
		basket3.translation.set(0, 0, -tabCentW + bkHoldW / 2);
		
		Node basket4 = childMB.node();
		basket4.id = "basket4";
		basket1.addChild(basket4);
		BoxShapeBuilder.build(childMB.part(basket4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), bkHoldW, bkHoldW, tabCentW);
		basket4.translation.set(tabCentW / 2 - bkHoldW / 2, 0, -tabCentW / 2);
		
		childMB.end();
		model = mb.end();
		model.calculateTransforms();
		
		modelInstance = new ModelInstance(model, x, y, z);
		
		mb.begin();
		childMB.begin();
		Node tabCenter1 = mb.node();
		tabCenter1.id = "tabCent1";
		BoxShapeBuilder.build(mb.part(tabCenter1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, centerMaterial), tabCentW, tabCentH, tabCentD);
		tabCenter1.translation.set(0, -tabH / 2 + tabCentH1, -tabD / 2 - tabCentD / 2);
		
		Node tabCenter2 = childMB.node();
		tabCenter2.id = "tabCent2";
		tabCenter1.addChild(tabCenter2);
		BoxShapeBuilder.build(childMB.part(tabCenter2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, centerMaterial), tabCentH, tabCentH1, tabCentD);
		tabCenter2.translation.set(-tabCentW / 2 + tabCentH / 2, -tabCentH1 / 2, 0);
		
		Node tabCenter3 = childMB.node();
		tabCenter3.id = "tabCent3";
		tabCenter1.addChild(tabCenter3);
		BoxShapeBuilder.build(childMB.part(tabCenter3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, centerMaterial), tabCentH, tabCentH1, tabCentD);
		tabCenter3.translation.set(tabCentW / 2 - tabCentH / 2, -tabCentH1 / 2, 0);
		
		childMB.end();
		tabCenter = new ModelInstance(mb.end(), getTabCenterTrans());
		
		matrixes.add(modelInstance.getNode(stand.id).globalTransform);
		matrixes.add(modelInstance.getNode(tab.id).globalTransform);
		matrixes.add(modelInstance.getNode(basketHold.id).globalTransform);
		matrixes.add(modelInstance.getNode(basket1.id).globalTransform);
		matrixes.add(modelInstance.getNode(basket2.id).globalTransform);
		matrixes.add(modelInstance.getNode(basket3.id).globalTransform);
		matrixes.add(modelInstance.getNode(basket4.id).globalTransform);
		
		tabCenterDimensions.set(tabCentW, tabCentH, tabCentD);
		/*matrixes.add(modelInstance.getNode(tabCenter1.id).globalTransform);
		matrixes.add(modelInstance.getNode(tabCenter2.id).globalTransform);
		matrixes.add(modelInstance.getNode(tabCenter3.id).globalTransform);*/
	}
	
	private Matrix4 getTabCenterTrans() {
		return calcTransformFromNodesTransform(modelInstance.getNode("tab").globalTransform.trn(0, 0, tabCentD));
	}
	
	private final Vector3 tabCenterDimensions = new Vector3();
	@Override
	public void render(ModelCache mCache) {
		super.render(mCache);
		
		//if (GameTools.isObjectVisibleToScreen(pCam, tabCenter, tabCenterDimensions)) {
			//mBatch.flush();
			mCache.add(tabCenter);
		//}
	}

	@Override
	protected void createCollisionShapes() {
		
		Vector3 bk13 = new Vector3(tabCentW / 2, bkHoldW / 2, bkHoldW / 2);
		Vector3 bk24 = new Vector3(bkHoldW / 2, bkHoldW / 2, tabCentW / 2);
		
		visibleCollShapes.add(new btCylinderShape(new Vector3(standW / 2, standH / 2, standD / 2)));
		visibleCollShapes.add(new btBoxShape(new Vector3(tabW / 2, tabH / 2, tabD / 2)));
		visibleCollShapes.add(new btBoxShape(new Vector3(bkHoldW / 2, bkHoldW / 2, bkHoldD / 2)));
		visibleCollShapes.add(new btBoxShape(bk13));
		visibleCollShapes.add(new btBoxShape(bk24));
		visibleCollShapes.add(new btBoxShape(bk13));
		visibleCollShapes.add(new btBoxShape(bk24));
		
		invisibleCollShapes.add(new btBoxShape(new Vector3(tabCentW / 2, bkHoldW / 2, tabCentW / 2)));
	}
	
	public void setRotation(float x, float y, float z, float angle) {
		super.setRotation(x, y, z, angle);
		
		if(!MyGdxGame.TESTING)
			recalcCollisionsTransform();
		
		manuallyRecalcCollisions();
	}

	@Override
	protected void specialFunction() {
		

	}

	@Override
	public float getWidth() {
		
		return tabW;
	}

	@Override
	public float getHeight() {
		
		return standH + tabH / 2;
	}

	@Override
	public float getDepth() {
		
		return standD + tabD + bkHoldD + tabCentD;
	}
	
	/*@Override
	public float getBoundingRadius() {
		return 8;
	}*/

	public Matrix4 getBasketTargetTrans() {
		return calcTransformFromNodesTransform(modelInstance.getNode("basket1").globalTransform.cpy().trn(0, 0, -tabCentW / 2));
	}

	@Override
	protected void setCollisions() {
		super.setCollisions();
		
		for(btRigidBody bo : bodies) {
			bo.setRestitution(0.4f);
			bo.setFriction(0f);
		}
		
		collisionObjects = new ArrayList<btCollisionObject>();
		
		for(int i = 0; i < invisibleCollShapes.size(); i++) {
			collisionObjects.add(new btCollisionObject());
			collisionObjects.get(i).setCollisionShape(invisibleCollShapes.get(i));
		}
		
		manuallySetObjects();
	}

	@Override
	protected void manuallyRecalcCollisions() {
		manuallySetObjects();
	}

	@Override
	protected void manuallySetObjects() {
		modelInstance.calculateTransforms();
		
		if(!MyGdxGame.TESTING)
			collisionObjects.get(0).setWorldTransform(getBasketTargetTrans());
		
		tabCenter.transform.set(getTabCenterTrans());
	}

	@Override
	protected void manuallySetBodies() {
		
	}

}
