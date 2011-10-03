package com.alt90.angle2;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * TileSet for TileMap
 * @author Ivan Pajuelo
 *
 */
public class AngleTileSet extends XMLUnmarshaller
{
	protected AngleTexture lTexture;
	private AngleTileMap lMap;
	protected int fFirstGid;
	protected AngleVectorI fTileSize_uu;
	private int fSpacing;
	private int fMargin;
	protected int fCols;
	protected int fRows;
	private boolean lOpacitySupport;

	AngleTileSet (AngleTileMap map, boolean opacitySupport)
	{
		lMap=map;
		lXMLTag="tileset";
		fFirstGid=0;
		fTileSize_uu=new AngleVectorI();
		fCols=0;
		fRows=0;
		lOpacitySupport=opacitySupport;
	}

	public boolean bindTexture(GL10 gl)
	{
		if (lTexture != null)
			return lTexture.bind(gl);
		return false;
	}

	public void fillTextureValues(int[] lTextureIV_tx, int tile, AngleVectorI uvDelta_tx, AngleVectorI tileSize_uu, byte flags)
	{
		if ((flags&AngleTileLayer.sHorizontalFlip)==AngleTileLayer.sHorizontalFlip)
		{
			//TODO Quick test. Need to be calculated
		   lTextureIV_tx[0] = ((tile%fCols)*(fTileSize_uu.fX+fSpacing)+fMargin+fTileSize_uu.fX-uvDelta_tx.fX); // Ucr
			lTextureIV_tx[2] = -tileSize_uu.fX; // Wcr
		}
		else
		{
		   lTextureIV_tx[0] = (int) ((tile%fCols)*(fTileSize_uu.fX+fSpacing)+fMargin+uvDelta_tx.fX); // Ucr
			lTextureIV_tx[2] = (int) tileSize_uu.fX; // Wcr
		}

		if ((flags&AngleTileLayer.sVerticalFlip)==AngleTileLayer.sVerticalFlip)
		{
			//TODO Quick test. Need to be calculated
		   lTextureIV_tx[1] = (int) ((tile/fCols)*(fTileSize_uu.fY+fSpacing)+fMargin+uvDelta_tx.fY); // Vcr
			lTextureIV_tx[3] = (int) tileSize_uu.fY; // Hcr
		}
		else
		{
		   lTextureIV_tx[1] = (int) ((tile/fCols)*(fTileSize_uu.fY+fSpacing)+fMargin+uvDelta_tx.fY+tileSize_uu.fY); // Vcr
			lTextureIV_tx[3] = (int) -tileSize_uu.fY; // Hcr
		}
	}

	public void fillTextureValues(float[] lTextureFV_tx, int tile, AngleVectorI uvDelta_tx, AngleVectorI tileSize_uu, byte flags)
	{
		if ((flags&AngleTileLayer.sHorizontalFlip)==AngleTileLayer.sHorizontalFlip)
		{
			//TODO Quick test. Need to be calculated
		   lTextureFV_tx[0] = ((tile%fCols)*(fTileSize_uu.fX+fSpacing)+fMargin+fTileSize_uu.fX-uvDelta_tx.fX)-0.001f; // Ucr
			lTextureFV_tx[2] = -tileSize_uu.fX+0.001f; // Wcr
		}
		else
		{
		   lTextureFV_tx[0] = (int) ((tile%fCols)*(fTileSize_uu.fX+fSpacing)+fMargin+uvDelta_tx.fX)+0.5f; // Ucr
			lTextureFV_tx[2] = (int) tileSize_uu.fX+0.5f; // Wcr
		}

		if ((flags&AngleTileLayer.sVerticalFlip)==AngleTileLayer.sVerticalFlip)
		{
			//TODO Quick test. Need to be calculated
		   lTextureFV_tx[1] = (int) ((tile/fCols)*(fTileSize_uu.fY+fSpacing)+fMargin+uvDelta_tx.fY); // Vcr
			lTextureFV_tx[3] = (int) tileSize_uu.fY; // Hcr
		}
		else
		{
		   lTextureFV_tx[1] = (int) ((tile/fCols)*(fTileSize_uu.fY+fSpacing)+fMargin+uvDelta_tx.fY+tileSize_uu.fY)+0.5f; // Vcr
			lTextureFV_tx[3] = (int) -tileSize_uu.fY-0.5f; // Hcr
		}
	}
	
	/**
	 * support for unmarshal TMX files
	 * You can found info about TMX format in http://mapeditor.org/
	 * Property of Thorbj�rn Lindeijer   
	 * @throws Exception 
	 */
	@Override
	protected void processAttribute(String param, String value) throws Exception
	{
		if (param.equals("firstgid"))
			fFirstGid=Integer.parseInt(value);
		else if (param.equals("source"))
			throw new Exception("TSX files not supported");
		else if (param.equals("tilewidth"))
			fTileSize_uu.fX=Integer.parseInt(value);
		else if (param.equals("tileheight"))
			fTileSize_uu.fY=Integer.parseInt(value);
		else if (param.equals("spacing"))
			fSpacing=Integer.parseInt(value);
		else if (param.equals("margin"))
			fMargin=Integer.parseInt(value);
	}

	@Override
	protected void processTag(String tag) throws Exception
	{
		if (tag.equals("image"))
		{
			readImage();
		}
		else
			skip(tag);
	}
	private void readImage() throws Exception
	{
		for (int t = 0; t < lXMLParser.getAttributeCount(); t++)
			processImageAttribute(lXMLParser.getAttributeName(t).toLowerCase(), lXMLParser.getAttributeValue(t).toLowerCase());
		
   }

	private void processImageAttribute(String param, String value) throws Exception
	{
		if (param.equals("source"))
		{
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
			InputStream is;
			Bitmap bitmap = null;
			try
			{
				is = AngleActivity.uInstance.getAssets().open(lMap.lPath+value);
				bitmap = BitmapFactory.decodeStream(is, null, bitmapOptions);
				is.close();
			}
			catch (IOException e)
			{
				throw new Exception ("Image "+lMap.lPath+value+" not found.");
			}
			fCols=(int) ((bitmap.getWidth()-fMargin)/(fTileSize_uu.fX+fSpacing));
			fRows=(int) ((bitmap.getHeight()-fMargin)/(fTileSize_uu.fY+fSpacing));
			bitmap.recycle();
			lTexture = AngleTextureEngine.createTextureFromAsset(lMap.lPath+value,(lOpacitySupport)?AngleTexture.TRANSLUCENT:AngleTexture.FASTEST);
		}
		else if (param.equals("trans"))
			throw new Exception ("Transparent color not supported. Use PNG32 instead.");
	}

}