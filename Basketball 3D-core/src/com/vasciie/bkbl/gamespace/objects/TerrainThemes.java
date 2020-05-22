/**
 * 
 */
package com.vasciie.bkbl.gamespace.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;

/**
 * Themes for the terrain for each specific gamemode
 * 
 * @author studi
 *
 */
public enum TerrainThemes {

	
	EASY{

		@Override
		public void createModels(Terrain terrain) {
			float wireSpace = 0.1f;
			float wireThickness = 0.01f;
			
			ModelBuilder mb = new ModelBuilder();
			
			Material material = new Material(ColorAttribute.createDiffuse(Color.BLACK));
			
			
			mb.begin();
			float temp;
			for(int i = 0; (temp = i * (wireSpace + wireThickness)) < terrain.getWidth(); i++) {
				Node node = mb.node();
				node.id = "fence" + i;
				node.translation.set(temp, (i + 1) * 1.1f, terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part("", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), 1, wireThickness, wireThickness);
			}
			model = mb.end();
			
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}
		
	},
	
	HARD{

		@Override
		public void createModels(Terrain terrain) {
			
			
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}
		
	},
	
	VERYHARD{

		@Override
		public void createModels(Terrain terrain) {
			
			
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}
		
	},
	
	CHALLENGE{

		@Override
		public void createModels(Terrain terrain) {
			
			
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}
		
	},
	
	MINIGAME{

		@Override
		public void createModels(Terrain terrain) {
			
			
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return false;
		}
		
	};
	
	private static Model model;
	
	
	public abstract void createModels(Terrain terrain);
	
	public abstract boolean hasOwnTerrain();
	
	public void dispose() {
		model.dispose();
	}
	
	public ModelInstance getModelInstance() {
		return new ModelInstance(model);
	}
	
}
