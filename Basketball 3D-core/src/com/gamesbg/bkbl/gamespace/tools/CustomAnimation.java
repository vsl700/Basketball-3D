package com.gamesbg.bkbl.gamespace.tools;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Used to make animations for a model through code
 * 
 * @author studi
 *
 */
public class CustomAnimation {
	Model model;
	//ArrayList<Animation> animations;
	
	int currentIndex;
	int currentNodeIndex;
	
	public CustomAnimation(Model model) {
		this.model = model;
		
		//animations = new ArrayList<Animation>();
	}
	
	public void addAnimation(String id, float duration) {
		Animation animation = new Animation();
		animation.id = id;
		animation.duration = duration;
		
		model.animations.add(animation);
		currentIndex = model.animations.size - 1;
	}
	
	public void addNodeAnimation(String nodeID) {
		NodeAnimation nodeAnim = new NodeAnimation();
		nodeAnim.node = model.getNode(nodeID);
		
		model.animations.get(currentIndex).nodeAnimations.add(nodeAnim);
		currentNodeIndex = model.animations.get(currentIndex).nodeAnimations.size - 1;
	}
	
	public void addTranslationKeyFrame(float x, float y, float z, float keyframe) {
		NodeAnimation nodeAnim = model.animations.get(currentIndex).nodeAnimations.get(currentNodeIndex);
		
		if(nodeAnim.translation == null)
			nodeAnim.translation = new Array<NodeKeyframe<Vector3>>();
		
		nodeAnim.translation.add(new NodeKeyframe<Vector3>(keyframe, new Vector3(x, y, z)));
	}
	
	public void addRotationKeyFrame(float x, float y, float z, float angle, float keyframe) {
		NodeAnimation nodeAnim = model.animations.get(currentIndex).nodeAnimations.get(currentNodeIndex);
		
		if(nodeAnim.rotation == null)
			nodeAnim.rotation = new Array<NodeKeyframe<Quaternion>>();
		
		nodeAnim.rotation.add(new NodeKeyframe<Quaternion>(keyframe, new Quaternion().setFromAxis(x, y, z, angle)));
	}
	
	/**
	 * Adds a new rotation keyframe where the rotation is on multiple axises, which have different angles
	 * @param axisAngles - [a rotation to add][0 - x, 1 - y, 2 - z, 3 - angle]
	 * @param keyframe - the animation keyframe
	 */
	public void addRotationsKeyFrame(float[][] axisAngles, float keyframe) {
		NodeAnimation nodeAnim = model.animations.get(currentIndex).nodeAnimations.get(currentNodeIndex);
		
		if(nodeAnim.rotation == null)
			nodeAnim.rotation = new Array<NodeKeyframe<Quaternion>>();
		
		Matrix4 temp = new Matrix4();
		for(int i = 0; i < axisAngles.length; i++)
			temp.rotate(axisAngles[i][0], axisAngles[i][1], axisAngles[i][2], axisAngles[i][3]);
		
		nodeAnim.rotation.add(new NodeKeyframe<Quaternion>(keyframe, new Quaternion().setFromMatrix(temp)));
	}
	
	//FIXME add a "addRotationsKeyFrame" method with the same arguments, but calculating the rotations by something like percents according to the given angles (probably according to the first angle)
	public void addRotationsKeyFrame2(float[][] axisAngles, float keyframe) {
		NodeAnimation nodeAnim = model.animations.get(currentIndex).nodeAnimations.get(currentNodeIndex);
		
		if(nodeAnim.rotation == null)
			nodeAnim.rotation = new Array<NodeKeyframe<Quaternion>>();
		
		
	}
	
	public void markAnimation(int index) {
		currentIndex = index;
	}
	
	public void markNodeAnimation(int index) {
		currentNodeIndex = index;
	}
}
