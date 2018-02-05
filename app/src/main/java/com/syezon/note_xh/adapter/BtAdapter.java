package com.syezon.note_xh.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syezon.note_xh.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */

public class BtAdapter extends RecyclerView.Adapter<BtAdapter.MyHolder> {

    private Context context;
    private List<ScanResult> devices = new ArrayList<>();
    private BluetoothClickListener listener;

    public BtAdapter() {}

    public BtAdapter(Context context, List<ScanResult> devices) {
        this.context = context;
        this.devices = devices;
    }

    public BtAdapter(Context context, List<ScanResult> devices, BluetoothClickListener listener) {
        this.context = context;
        this.devices = devices;
        this.listener = listener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bluetooth_device, parent, false);
        return new MyHolder(view);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final ScanResult device = devices.get(position);
        holder.tv.setText(device.SSID);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.click(device);
            }
        });
    }

    class MyHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_bluetooth)
        TextView tv;
        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface BluetoothClickListener{
        void click(ScanResult device);
    }
}
