package com.vasciie.bkbl.gamespace.entities;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.players.Opponent;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;
import com.vasciie.bkbl.gamespace.tools.CustomAnimation;

public enum EntityType {
	
	BALL(Ball.class, "ball", 1) {
		@Override
		public Model createEntityModel() {
			ModelBuilder mb = new ModelBuilder();
			//MeshPartBuilder meshBuilder;
			
			Texture ballTexture = new Texture(Gdx.files.internal("game/basketball_3d_texture.jpg"));
			ballTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
			ballTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			
			Material material = new Material(TextureAttribute.createDiffuse(ballTexture));
			
			Model model = mb.createSphere(1, 1, 1, 23, 23, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
			model.manageDisposable(ballTexture);
			
			return model;
		}
	},
	
	TEAMMATE(Teammate.class, "team", 30) {
		private void animateModel(Model model) {
			CustomAnimation custom = new CustomAnimation(model);
			
			//We needed an idle animation for all of the body parts that don't act in any way and also to animate transitions between animation and idle staying for legs and body
			custom.addAnimation("idle", 0);
			custom.addNodeAnimation("spine2");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			
			
			custom.addAnimation("stayArmL", 4);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(-1, 0, 1, 3, 2);
			custom.addRotationKeyFrame(0, 0, 0, 0, 4);
			
			
			custom.addAnimation("stayArmR", 4);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationKeyFrame(-1, 0, 0, 5, 0);
			custom.addRotationKeyFrame(0, 0, -1, 3, 2);
			custom.addRotationKeyFrame(-1, 0, 0, 5, 4);
			
			
			//Aimings are going to be animated by the controller's fading effect
			custom.addAnimation("aimLArmL", 0.01f);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationKeyFrame(0, 1.2f, 1.4f, 90, 0);
			//custom.addRotationKeyFrame(1, 0, 0, 30, 0);
			custom.addRotationKeyFrame(0, 1.2f, 1.4f, 90, 0.01f);
			//custom.addRotationKeyFrame(0, 1, 0, 110, 0);
			//custom.addRotationKeyFrame(1, 0, 0, 30, 0);
			//custom.addRotationKeyFrame(0, 1, 0, 110, 1);
			
			custom.addNodeAnimation("elbowL");
			custom.addRotationKeyFrame(1, 0, 0, -60, 0);
			custom.addRotationKeyFrame(1, 0, 0, -60, 0.01f);
			
			
			custom.addAnimation("aimLArmR", 0.01f);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, 110}, {1, 0, 1, 70}}, 0);
			
			
			custom.addAnimation("aimLBody", 0.01f);
			
			custom.addNodeAnimation("spine2");
			custom.addRotationKeyFrame(0, 1, 0, 30, 0);
			custom.addRotationKeyFrame(0, 1, 0, 30, 0.01f);
			
			custom.addNodeAnimation("spine1");
			custom.addRotationKeyFrame(0, 1, 0, 30, 0);
			custom.addRotationKeyFrame(0, 1, 0, 30, 0.01f);
			
			
			custom.addAnimation("throwLArmL", 0.25f);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, -110}, {1, 0, 1, 70}}, 0);
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, -110}, {1, 0, 1, 70}}, 0.25f);
			
			custom.addNodeAnimation("elbowL");
			custom.addRotationKeyFrame(1, 0, 0, 0, 0);
			custom.addRotationKeyFrame(1, 0, 0, 0, 0.25f);
			
			
			custom.addAnimation("throwLArmR", 0.25f);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, 40}, {0, 1, 0, 110}, {1, 0, 1, 50}}, 0);
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, 40}, {0, 1, 0, 110}, {1, 0, 1, 50}}, 0.25f);
			
			
			custom.addAnimation("throwLBody", 0.25f);
			
			custom.addNodeAnimation("spine1");
			custom.addRotationKeyFrame(0, 1, 0, -30, 0);
			custom.addRotationKeyFrame(0, 1, 0, -30, 0.25f);
			
			custom.addNodeAnimation("spine2");
			custom.addRotationKeyFrame(0, 1, 0, -30, 0);
			custom.addRotationKeyFrame(0, 1, 0, -30, 0.25f);
			
			
			custom.addAnimation("aimRArmL", 0.01f);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationsKeyFrame(new float[][]{{0, 1, 0, -70}, {1, 0, 0, 40}, {1, 0, 1, 70}, {0, 1, 0, 90}}, 0);
			
			
			custom.addAnimation("aimRArmR", 0.01f);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationKeyFrame(0, 1.2f, 1.4f, -90, 0);
			custom.addRotationKeyFrame(0, 1.2f, 1.4f, -90, 0.01f);
			
			custom.addNodeAnimation("elbowR");
			custom.addRotationKeyFrame(1, 0, 0, -60, 0);
			custom.addRotationKeyFrame(1, 0, 0, -60, 0.01f);
			
			
			custom.addAnimation("aimRBody", 0.01f);
			
			custom.addNodeAnimation("spine2");
			custom.addRotationKeyFrame(0, 1, 0, -30, 0);
			custom.addRotationKeyFrame(0, 1, 0, -30, 0.01f);
			
			custom.addNodeAnimation("spine1");
			custom.addRotationKeyFrame(0, 1, 0, -30, 0);
			custom.addRotationKeyFrame(0, 1, 0, -30, 0.01f);
			
			
			custom.addAnimation("throwRArmL", 0.25f);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, 40}, {0, 1, 0, 110}, {1, 0, 1, 50}}, 0);
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, 40}, {0, 1, 0, 110}, {1, 0, 1, 50}}, 0.25f);
			
			custom.addAnimation("throwRArmR", 0.25f);
			custom.addNodeAnimation("shoulderR");
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, 110}, {1, 0, 1, 70}, {0, 0, 1, -60}}, 0);
			custom.addRotationsKeyFrame(new float[][]{{1, 0, 0, -140}, {0, 1, 0, 110}, {1, 0, 1, 70}, {0, 0, 1, -60}}, 0.25f);
			
			custom.addNodeAnimation("elbowR");
			custom.addRotationKeyFrame(1, 0, 0, 0, 0);
			custom.addRotationKeyFrame(1, 0, 0, 0, 0.25f);
			
			
			custom.addAnimation("throwRBody", 0.25f);
			
			custom.addNodeAnimation("spine1");
			custom.addRotationKeyFrame(0, 1, 0, 30, 0);
			custom.addRotationKeyFrame(0, 1, 0, 30, 0.25f);
			
			custom.addNodeAnimation("spine2");
			custom.addRotationKeyFrame(0, 1, 0, 30, 0);
			custom.addRotationKeyFrame(0, 1, 0, 30, 0.25f);
			
			
			custom.addAnimation("dribbleIdleArmL", 1);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationKeyFrame(1, 0, 0, -10, 0);
			custom.addRotationKeyFrame(1, 0, 0, -10, 1);
			
			custom.addNodeAnimation("elbowL");
			custom.addRotationKeyFrame(1, 0, 0, -80, 0);
			custom.addRotationKeyFrame(1, 0, 0, -80, 1);
			
			
			custom.addAnimation("dribblePhase1ArmL", Player.dribbleSpeed);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationKeyFrame(1, 0, 0, -10, 0);
			custom.addRotationKeyFrame(1, 0, 0, -10, Player.dribbleSpeed);
			
			custom.addNodeAnimation("elbowL");
			custom.addRotationKeyFrame(1, 0, 0, -80, 0);
			custom.addRotationKeyFrame(1, 0, 0, -50, Player.dribbleSpeed);
			
			
			custom.addAnimation("dribbleIdle1ArmL", 1);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationKeyFrame(1, 0, 0, -10, 0);
			custom.addRotationKeyFrame(1, 0, 0, -10, 1);
			
			custom.addNodeAnimation("elbowL");
			custom.addRotationKeyFrame(1, 0, 0, -50, 0);
			custom.addRotationKeyFrame(1, 0, 0, -50, 1);
			
			
			custom.addAnimation("dribblePhase2ArmL", Player.dribbleSpeed);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationKeyFrame(1, 0, 0, -10, 0);
			custom.addRotationKeyFrame(1, 0, 0, -10, Player.dribbleSpeed);
			
			custom.addNodeAnimation("elbowL");
			custom.addRotationKeyFrame(1, 0, 0, -50, 0);
			custom.addRotationKeyFrame(1, 0, 0, -80, Player.dribbleSpeed);
			
			
			custom.addAnimation("dribbleIdleArmR", 1);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationKeyFrame(1, 0, 0, -10, 0);
			custom.addRotationKeyFrame(1, 0, 0, -10, 1);
			
			custom.addNodeAnimation("elbowR");
			custom.addRotationKeyFrame(1, 0, 0, -80, 0);
			custom.addRotationKeyFrame(1, 0, 0, -80, 1);
			
			
			custom.addAnimation("dribblePhase1ArmR", Player.dribbleSpeed);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationKeyFrame(1, 0, 0, -10, 0);
			custom.addRotationKeyFrame(1, 0, 0, -10, Player.dribbleSpeed);
			
			custom.addNodeAnimation("elbowR");
			custom.addRotationKeyFrame(1, 0, 0, -80, 0);
			custom.addRotationKeyFrame(1, 0, 0, -50, Player.dribbleSpeed);
			
			
			custom.addAnimation("dribbleIdle1ArmR", 1);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationKeyFrame(1, 0, 0, -10, 0);
			custom.addRotationKeyFrame(1, 0, 0, -10, 1);
			
			custom.addNodeAnimation("elbowR");
			custom.addRotationKeyFrame(1, 0, 0, -50, 0);
			custom.addRotationKeyFrame(1, 0, 0, -50, 1);
			
			
			custom.addAnimation("dribblePhase2ArmR", Player.dribbleSpeed);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationKeyFrame(1, 0, 0, -10, 0);
			custom.addRotationKeyFrame(1, 0, 0, -10, Player.dribbleSpeed);
			
			custom.addNodeAnimation("elbowR");
			custom.addRotationKeyFrame(1, 0, 0, -50, 0);
			custom.addRotationKeyFrame(1, 0, 0, -80, Player.dribbleSpeed);
			
			
			custom.addAnimation("runBody", 0.75f);
			
			custom.addNodeAnimation("spine3");
			custom.addRotationKeyFrame(1, 0, 0, 20, 0);
			custom.addRotationKeyFrame(1, 0, 0, 20, 0.75f);
			
			custom.addAnimation("runArmL", 0.75f);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(-1, 0, 0, 50, 0.15f);
			custom.addRotationKeyFrame(1, 0, 0, 40, 0.6f);
			custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
			
			custom.addNodeAnimation("elbowL");
			custom.addRotationKeyFrame(-1, 0, 0, 90, 0);
			custom.addRotationKeyFrame(-1, 0, 0, 90, 0.75f);
			
			custom.addAnimation("runArmR", 0.75f);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(1, 0, 0, 40, 0.25f);
			custom.addRotationKeyFrame(-1, 0, 0, 50, 0.6f);
			custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
			
			custom.addNodeAnimation("elbowR");
			custom.addRotationKeyFrame(-1, 0, 0, 90, 0);
			custom.addRotationKeyFrame(-1, 0, 0, 90, 0.75f);
			
			custom.addAnimation("runLegL", 0.75f);
			
			custom.addNodeAnimation("hipL");
			custom.addRotationKeyFrame(-1, 0, 0, 20, 0);
			custom.addRotationKeyFrame(1, 0, 0, 50, 0.15f);
			custom.addRotationKeyFrame(-1, 0, 0, 90, 0.6f);
			custom.addRotationKeyFrame(-1, 0, 0, 20, 0.75f);
			
			custom.addNodeAnimation("kneeL");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(0, 0, 0, 0, 0.15f);
			custom.addRotationKeyFrame(1, 0, 0, 90, 0.6f);
			custom.addRotationKeyFrame(0, 0, 0, 0, 0.75f);
			
			custom.addAnimation("runLegR", 0.75f);
			
			custom.addNodeAnimation("hipR");
			custom.addRotationKeyFrame(-1, 0, 0, 20, 0);
			custom.addRotationKeyFrame(-1, 0, 0, 90, 0.15f);
			custom.addRotationKeyFrame(1, 0, 0, 50, 0.6f);
			custom.addRotationKeyFrame(-1, 0, 0, 20, 0.75f);
			
			custom.addNodeAnimation("kneeR");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(1, 0, 0, 90, 0.15f);
			custom.addRotationKeyFrame(1, 0, 0, 0, 0.6f);
			custom.addRotationKeyFrame(0, 0, 0, 00, 0.75f);
			
			
			custom.addAnimation("walkArmL", 1);
			
			custom.addNodeAnimation("shoulderL");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.25f);
			custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
			custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.75f);
			custom.addRotationKeyFrame(0, 0, 0, 50, 1);
			
			custom.addNodeAnimation("elbowL");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(0, 0, 0, 0, 0.5f);
			custom.addRotationKeyFrame(-0.25f, 0, 0, 60, 0.75f);
			custom.addRotationKeyFrame(0, 0, 0, 60, 1);
			
			custom.addAnimation("walkArmR", 1);
			
			custom.addNodeAnimation("shoulderR");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.25f);
			custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
			custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.75f);
			custom.addRotationKeyFrame(0, 0, 0, 50, 1);
			
			custom.addNodeAnimation("elbowR");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(-0.25f, 0, 0, 60, 0.25f);
			custom.addRotationKeyFrame(0, 0, 0, 60, 0.5f);
			custom.addRotationKeyFrame(0, 0, 0, 0, 1);
			
			custom.addAnimation("walkLegL", 1);
			
			custom.addNodeAnimation("hipL");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.25f);
			custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
			custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.75f);
			custom.addRotationKeyFrame(0, 0, 0, 50, 1);
			
			custom.addNodeAnimation("kneeL");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(0.25f, 0, 0, 20, 0.25f);
			custom.addRotationKeyFrame(0, 0, 0, 20, 0.5f);
			custom.addRotationKeyFrame(0, 0, 0, 0, 1);
			
			custom.addAnimation("walkLegR", 1);
			
			custom.addNodeAnimation("hipR");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(0.5f, 0, 0, 30, 0.25f);
			custom.addRotationKeyFrame(0, 0, 0, 50, 0.5f);
			custom.addRotationKeyFrame(-0.5f, 0, 0, 30, 0.75f);
			custom.addRotationKeyFrame(0, 0, 0, 50, 1);


			custom.addNodeAnimation("kneeR");
			custom.addRotationKeyFrame(0, 0, 0, 0, 0);
			custom.addRotationKeyFrame(0, 0, 0, 0, 0.5f);
			custom.addRotationKeyFrame(0.25f, 0, 0, 20, 0.75f);
			custom.addRotationKeyFrame(0, 0, 0, 20, 1);
			
		}
		
		@Override
		public Model createEntityModel() {
			final float scale1 = Player.scale1;
			final float scale2 = Player.scale2;
			final float scale3 = Player.scale3;
			final float scale4 = Player.scale4;
			final float scale5 = Player.scale5;
			final float scale6 = Player.scale6;
			final float scale7 = Player.scale7;
			final float handPercentage = Player.handPercentage;
			
			int divisionUHead = 8;
			int divisionVHead = 8;
			int divisionU = 8;
			int divisionV = 8;
			int divisionU2 = 4;
			int divisionV2 = 4;
			
			ModelBuilder mb = new ModelBuilder();
			ModelBuilder childMB = new ModelBuilder();
			Material material = new Material(ColorAttribute.createDiffuse(Color.BLUE));
			
			mb.begin();
			childMB.begin();
			
			Node head = childMB.node();
			head.id = "head";
			head.translation.set(0, scale1 / 2 + scale3 / 2 + 0.1f, 0);
			SphereShapeBuilder.build(childMB.part(head.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale1, scale1, scale1, divisionUHead, divisionVHead);
			
			
			Node spine1 = childMB.node();
			spine1.id = "spine1";
			spine1.translation.set(0, scale3 * 3.5f, 0);
			BoxShapeBuilder.build(childMB.part(spine1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
			spine1.addChild(head);
			
			Node spine2 = childMB.node();
			spine2.id = "spine2";
			spine2.translation.set(0, scale3 * 3.5f, 0);
			BoxShapeBuilder.build(childMB.part(spine2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
			spine2.addChild(spine1);
			
			Node spine3 = mb.node();
			spine3.id = "spine3";
			BoxShapeBuilder.build(mb.part(spine3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale2, scale3, scale4);
			spine3.addChild(spine2);
			
			Node shoulderL = childMB.node();
			shoulderL.id = "shoulderL";
			shoulderL.translation.set(scale2 / 2 + scale6 / 2, 0, 0);
			SphereShapeBuilder.build(childMB.part(shoulderL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale6, scale6, scale6, divisionU, divisionV);
			spine1.addChild(shoulderL);
			
			Node arm1L = childMB.node();
			arm1L.id = "arm1L";
			shoulderL.addChild(arm1L);
			arm1L.translation.set(0, -scale5 / 2, 0);
			BoxShapeBuilder.build(childMB.part(arm1L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
			
			Node elbowL = childMB.node();
			elbowL.id = "elbowL";
			arm1L.addChild(elbowL);
			elbowL.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
			SphereShapeBuilder.build(childMB.part(elbowL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU2, divisionV2);
			
			Node arm2L = childMB.node();
			arm2L.id = "arm2L";
			elbowL.addChild(arm2L);
			arm2L.translation.set(0, -scale5 * (1 - handPercentage) / 2 - scale3 / 2, scale3 * scale5);
			BoxShapeBuilder.build(childMB.part(arm2L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * (1 - handPercentage), scale4);
			
			Node handL = childMB.node();
			handL.id = "handL";
			arm2L.addChild(handL);
			handL.translation.set(0, -scale5 / 2, 0);
			BoxShapeBuilder.build(childMB.part(handL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * handPercentage, scale4);
			
			Node shoulderR = childMB.node();
			shoulderR.id = "shoulderR";
			shoulderR.translation.set(-scale2 / 2 - scale6 / 2, 0, 0);
			SphereShapeBuilder.build(childMB.part(shoulderR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale6, scale6, scale6, divisionU, divisionV);
			spine1.addChild(shoulderR);
			
			Node arm1R = childMB.node();
			arm1R.id = "arm1R";
			shoulderR.addChild(arm1R);
			arm1R.translation.set(0, -scale5 / 2, 0);
			BoxShapeBuilder.build(childMB.part(arm1R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
			
			Node elbowR = childMB.node();
			elbowR.id = "elbowR";
			arm1R.addChild(elbowR);
			elbowR.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
			SphereShapeBuilder.build(childMB.part(elbowR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU2, divisionV2);
			
			Node arm2R = childMB.node();
			arm2R.id = "arm2R";
			elbowR.addChild(arm2R);
			arm2R.translation.set(0, -scale5 * (1 - handPercentage) / 2 - scale3 / 2, scale3 * scale5);
			BoxShapeBuilder.build(childMB.part(arm2R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * (1 - handPercentage), scale4);
			
			Node handR = childMB.node();
			handR.id = "handR";
			arm2R.addChild(handR);
			handR.translation.set(0, -scale5 / 2, 0);
			BoxShapeBuilder.build(childMB.part(handR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5 * handPercentage, scale4);
			
			Node hipL = childMB.node();
			hipL.id = "hipL";
			hipL.translation.set(scale2 / 2 - scale3 / 2, 0, 0);
			SphereShapeBuilder.build(childMB.part(hipL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale7, scale7, scale7, divisionU, divisionV);
			spine3.addChild(hipL);
			
			Node leg1L = childMB.node();
			leg1L.id = "leg1L";
			hipL.addChild(leg1L);
			leg1L.translation.set(0, -scale5 / 2 - scale7 / 3, 0);
			BoxShapeBuilder.build(childMB.part(leg1L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
			
			Node kneeL = childMB.node();
			kneeL.id = "kneeL";
			leg1L.addChild(kneeL);
			kneeL.translation.set(0, -scale5 / 2 - scale3 / 2, scale3 * scale5);
			SphereShapeBuilder.build(childMB.part(kneeL.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU2, divisionV2);
			
			Node leg2L = childMB.node();
			leg2L.id = "leg2L";
			kneeL.addChild(leg2L);
			leg2L.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
			BoxShapeBuilder.build(childMB.part(leg2L.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
			
			Node hipR = childMB.node();
			hipR.id = "hipR";
			hipR.translation.set(-scale2 / 2 + scale3 / 2, 0, 0);
			SphereShapeBuilder.build(childMB.part(hipR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale7, scale7, scale7, divisionU, divisionV);
			spine3.addChild(hipR);
			
			Node leg1R = childMB.node();
			leg1R.id = "leg1R";
			hipR.addChild(leg1R);
			leg1R.translation.set(0, -scale5 / 2 - scale7 / 3, 0);
			BoxShapeBuilder.build(childMB.part(leg1R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
			
			Node kneeR = childMB.node();
			kneeR.id = "kneeR";
			leg1R.addChild(kneeR);
			kneeR.translation.set(0, -scale5 / 2 - scale3 / 2, scale3 * scale5);
			SphereShapeBuilder.build(childMB.part(kneeR.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale3, scale3, divisionU2, divisionV2);
			
			Node leg2R = childMB.node();
			leg2R.id = "leg2R";
			kneeR.addChild(leg2R);
			leg2R.translation.set(0, -scale5 / 2 - scale3 / 2, -scale3 * scale5);
			BoxShapeBuilder.build(childMB.part(leg2R.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), scale3, scale5, scale4);
			
			childMB.end();
			Model model = mb.end();
			
			animateModel(model);
			
			return model;
		}
	},
	
	OPPONENT(Opponent.class, "opp", 30) {
		@Override
		public Model createEntityModel() {
			Model temp = TEAMMATE.createEntityModel();
			temp.materials.get(0).set(new Material(ColorAttribute.createDiffuse(Color.RED)));
			
			return temp;
		}
	};
	
	@SuppressWarnings("rawtypes")
	private Class loaderClass;
	private String id;
	private float mass;
	private Model model;
	
	private EntityType(@SuppressWarnings("rawtypes") Class loaderClass, String id, float mass) {
		this.loaderClass = loaderClass;
		this.id = id;
		this.mass = mass;
		model = createEntityModel();
	}

	@SuppressWarnings("rawtypes")
	public Class getLoaderClass() {
		return loaderClass;
	}

	public String getId() {
		return id;
	}
	
	public float getMass() {
		return mass;
	}
	
	public Model getModel() {
		return model;
	}
	
	public abstract Model createEntityModel(); 
	
	public static Player createPlayer(String id, GameMap map, Vector3 pos){
		EntityType type = entityTypes.get(id); 
		try {
			@SuppressWarnings("unchecked")
			Player player = (Player) ClassReflection.newInstance(type.loaderClass);
			player.create(type, map, pos);
			return player;
		} catch (ReflectionException e) {
			Gdx.app.error("Entity Loader", "Could not load entity of type " + type.id);
			return null;
		}
	}
	
	public static Entity createEntity(String id, GameMap map, Vector3 pos){
		EntityType type = entityTypes.get(id);
		try {
			@SuppressWarnings("unchecked")
			Entity entity = (Entity) ClassReflection.newInstance(type.loaderClass);
			entity.create(type, map, pos);
			return entity;
		} catch (ReflectionException e) {
			Gdx.app.error("Entity Loader", "Could not load entity of type " + type.id);
			return null;
		}
	}

	private static HashMap<String, EntityType> entityTypes;

	static{
		entityTypes = new HashMap<String, EntityType>();
		for(EntityType type : EntityType.values())
			entityTypes.put(type.id, type);

	}
	
}
