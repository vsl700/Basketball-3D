/**
 * 
 */
package com.vasciie.bkbl.gamespace.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
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
			float wallHeight = 15;
			float wallDepth = 0.1f, wallDepth2 = 0.05f;
			float wallSpace = 2.5f, wallSpace2 = 4;
			
			ModelBuilder mb = new ModelBuilder();
			//PLAYGROUND
			Material material = new Material(ColorAttribute.createDiffuse(Color.GRAY.cpy().add(0.3f, 0.3f, 0.3f, 0)));
			
			
			mb.begin();
			float temp;
			for (int i = 0; (temp = -terrain.getWidth() / 2 + i * wallSpace) <= terrain.getWidth() / 2; i++) {
				Node wall = mb.node();
				wall.id = "fenceWall1." + i;
				wall.translation.set(temp, wallHeight / 2, terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.id = "fenceWallH1." + i;
				wall.translation.set(0, temp, terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth2, wallDepth2);
			}
			
			Node wallUpper = mb.node();
			wallUpper.id = "fenceWall1.Upper";
			wallUpper.translation.set(0, wallHeight, terrain.getDepth() / 2);
			BoxShapeBuilder.build(mb.part(wallUpper.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth, wallDepth);
			
			
			for (int i = 0; (temp = -terrain.getDepth() / 2 + i * wallSpace) <= terrain.getDepth() / 2; i++) {
				Node wall = mb.node();
				wall.id = "fenceWall2." + i;
				wall.translation.set(terrain.getWidth() / 2, wallHeight / 2, temp);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.id = "fenceWallH2." + i;
				wall.translation.set(terrain.getWidth() / 2, temp, 0);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth2, wallDepth2, terrain.getDepth());
			}
			
			Node wallUpper2 = mb.node();
			wallUpper2.id = "fenceWall2.Upper";
			wallUpper2.translation.set(terrain.getWidth() / 2, wallHeight, 0);
			BoxShapeBuilder.build(mb.part(wallUpper2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallDepth, terrain.getDepth());
			
			
			for (int i = 0; (temp = -terrain.getWidth() / 2 + i * wallSpace) <= terrain.getWidth() / 2; i++) {
				Node wall = mb.node();
				wall.id = "fenceWall3." + i;
				wall.translation.set(temp, wallHeight / 2, -terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.id = "fenceWallH3." + i;
				wall.translation.set(0, temp, -terrain.getDepth() / 2);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth2, wallDepth2);
			}
			
			Node wallUpper3 = mb.node();
			wallUpper3.id = "fenceWall3.Upper";
			wallUpper3.translation.set(0, wallHeight, -terrain.getDepth() / 2);
			BoxShapeBuilder.build(mb.part(wallUpper3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth(), wallDepth, wallDepth);
			
			
			for (int i = 0; (temp = -terrain.getDepth() / 2 + i * wallSpace) <= terrain.getDepth() / 2; i++) {
				Node wall = mb.node();
				wall.id = "fenceWall4." + i;
				wall.translation.set(-terrain.getWidth() / 2, wallHeight / 2, temp);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallHeight, wallDepth);
			}
			
			for (int i = 1; (temp = wallHeight - i * wallSpace2) >= 0; i++) {
				Node wall = mb.node();
				wall.id = "fenceWallH4." + i;
				wall.translation.set(-terrain.getWidth() / 2, temp, 0);
				BoxShapeBuilder.build(mb.part(wall.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth2, wallDepth2, terrain.getDepth());
			}
			
			Node wallUpper4 = mb.node();
			wallUpper4.id = "fenceWall4.Upper";
			wallUpper4.translation.set(-terrain.getWidth() / 2, wallHeight, 0);
			BoxShapeBuilder.build(mb.part(wallUpper4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), wallDepth, wallDepth, terrain.getDepth());
			
			
			Node cityPlate = mb.node();
			cityPlate.id = "cityPlate";
			cityPlate.translation.set(0, -0.001f, 0);
			BoxShapeBuilder.build(mb.part(wallUpper4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material), terrain.getWidth() * 2, 0.01f, terrain.getDepth() * 2);
			
			//STREETS
			float streetWidth = 11f, streetMarkWidth = 1f, streetMarkDepth = 4.5f, streetMarkSpace = 3;
			
			Material materialStreet = new Material(ColorAttribute.createDiffuse(Color.GRAY));
			Material materialStreetMark = new Material(ColorAttribute.createDiffuse(Color.WHITE));
			
			Node street1 = mb.node();
			street1.translation.set(terrain.getWidth() + streetWidth / 2, cityPlate.translation.y, 0);
			BoxShapeBuilder.build(mb.part(street1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreet), streetWidth, 0.01f, terrain.getDepth() * 8 + streetWidth * 2);
			
			for(int i = 0; (temp = -terrain.getDepth() + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace)) <= terrain.getDepth() - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(terrain.getWidth() + streetWidth / 2, cityPlate.translation.y, temp);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, 0.05f, streetMarkDepth);
			
				Node streetMark1 = mb.node();
				streetMark1.translation.set(-(terrain.getWidth() + streetWidth / 2), cityPlate.translation.y, temp);
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, 0.05f, streetMarkDepth);
			}
			
			Node street2 = mb.node();
			street2.translation.set(0, cityPlate.translation.y, terrain.getDepth() + streetWidth / 2);
			BoxShapeBuilder.build(mb.part(street2.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreet), terrain.getWidth() * 8 + streetWidth * 2, 0.01f, streetWidth);
			
			for(int i = 0; (temp = -terrain.getWidth() + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace)) <= terrain.getWidth() - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(temp, cityPlate.translation.y, terrain.getDepth() + streetWidth / 2);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, 0.05f, streetMarkWidth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(temp, cityPlate.translation.y, -(terrain.getDepth() + streetWidth / 2));
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, 0.05f, streetMarkWidth);
			}
			
			Node street3 = mb.node();
			street3.translation.set(-(terrain.getWidth() + streetWidth / 2), cityPlate.translation.y, 0);
			BoxShapeBuilder.build(mb.part(street3.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreet), streetWidth, 0.01f, terrain.getDepth() * 8 + streetWidth * 2);
			
			Node street4 = mb.node();
			street4.translation.set(0, cityPlate.translation.y, -(terrain.getDepth() + streetWidth / 2));
			BoxShapeBuilder.build(mb.part(street4.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreet), terrain.getWidth() * 8 + streetWidth * 2, 0.01f, streetWidth);
			
			
			for(int i = 0; (temp = terrain.getDepth() + streetWidth + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace)) <= terrain.getDepth() * 4 - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(terrain.getWidth() + streetWidth / 2, cityPlate.translation.y, temp);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, 0.05f, streetMarkDepth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(-(terrain.getWidth() + streetWidth / 2), cityPlate.translation.y, temp);
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, 0.05f, streetMarkDepth);
			}
			
			for(int i = 0; (temp = terrain.getWidth() + streetWidth + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace)) <= terrain.getWidth() * 4 - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(temp, cityPlate.translation.y, terrain.getDepth() + streetWidth / 2);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, 0.05f, streetMarkWidth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(temp, cityPlate.translation.y, -(terrain.getDepth() + streetWidth / 2));
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, 0.05f, streetMarkWidth);
			}
			
			for(int i = 0; (temp = -(terrain.getDepth() + streetWidth + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace))) >= -terrain.getDepth() * 4 - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(terrain.getWidth() + streetWidth / 2, cityPlate.translation.y, temp);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, 0.05f, streetMarkDepth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(-(terrain.getWidth() + streetWidth / 2), cityPlate.translation.y, temp);
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkWidth, 0.05f, streetMarkDepth);
			}
			
			for(int i = 0; (temp = -(terrain.getWidth() + streetWidth + streetMarkSpace + i * (streetMarkDepth + streetMarkSpace))) >= -terrain.getWidth() * 4 - streetMarkSpace; i++) {
				Node streetMark = mb.node();
				streetMark.translation.set(temp, cityPlate.translation.y, terrain.getDepth() + streetWidth / 2);
				BoxShapeBuilder.build(mb.part(streetMark.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, 0.05f, streetMarkWidth);
				
				Node streetMark1 = mb.node();
				streetMark1.translation.set(temp, cityPlate.translation.y, -(terrain.getDepth() + streetWidth / 2));
				BoxShapeBuilder.build(mb.part(streetMark1.id, GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, materialStreetMark), streetMarkDepth, 0.05f, streetMarkWidth);
			}
			
			model = mb.end();
			
			//NEW TERRAIN
			Texture court = new Texture(Gdx.files.internal("game/basketball_court_easy.jpg"));
			court.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
			court.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

			Material materialCourt = new Material(TextureAttribute.createDiffuse(court));
			long attribs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

			mb.begin();
			Node terrainNode = mb.node();
			BoxShapeBuilder.build(mb.part(terrainNode.id, GL20.GL_TRIANGLES, attribs, materialCourt), terrain.getWidth(), 0.01f, terrain.getDepth());
			customTerrainModel = mb.end();
			customTerrainModel.manageDisposable(court);
		}

		@Override
		public boolean hasOwnTerrain() {
			
			return true;
		}

		@Override
		public Color getThemeColor() {
			
			return //new Color(0.6f, 0.5f, 0, 1);
					null;
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

		@Override
		public Color getThemeColor() {
			
			return null;
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

		@Override
		public Color getThemeColor() {
			
			return null;
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

		@Override
		public Color getThemeColor() {
			
			return null;
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

		@Override
		public Color getThemeColor() {
			
			return null;
		}
		
	};
	
	private static Model model, customTerrainModel;
	
	
	public abstract void createModels(Terrain terrain);
	
	public abstract Color getThemeColor();
	
	public abstract boolean hasOwnTerrain();
	
	public void dispose() {
		model.dispose();
		
		if(customTerrainModel != null)
			customTerrainModel.dispose();
	}
	
	public ModelInstance getModelInstance() {
		return new ModelInstance(model, 0, 0, 0);
	}
	
	public ModelInstance getCustomTerrainModelInstance() {
		if(hasOwnTerrain())
			return new ModelInstance(customTerrainModel, 0, 0, 0);
		
		return null;
	}
	
}
