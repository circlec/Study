package com.aprilbrother.aprilbrothersdk.connection;

import android.bluetooth.BluetoothGattCharacteristic;

public abstract interface BluetoothService {
	public abstract void update(
            BluetoothGattCharacteristic paramBluetoothGattCharacteristic);
}
