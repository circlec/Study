package com.wistive.travel;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.amap.api.maps.model.Tile;
import com.amap.api.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @作者 zhouchao
 * @日期 2019/4/4
 * @描述
 */
public abstract class AssetTileProvider implements TileProvider {
    private static final String TAG = AssetTileProvider.class.getSimpleName();
    private final int TILE_WIDTH = 256;
    private final int TILE_HEIGHT = 256;
    public static final int BUFFER_SIZE = 16 * 1024;
    public AssetManager assetManager;

    public AssetTileProvider(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        byte[] image = readTileImage(x, y, zoom);
        return image == null ? NO_TILE : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
    }

    private byte[] readTileImage(int x, int y, int zoom) {
        byte[] image = null;
        try {
            InputStream inputStream =assetManager.open("map/" + zoom + "/" + x + "_" + y + ".png");
            image = input2byte(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    @Override
    public int getTileWidth() {
        return TILE_WIDTH;
    }

    @Override
    public int getTileHeight() {
        return TILE_HEIGHT;
    }
}
