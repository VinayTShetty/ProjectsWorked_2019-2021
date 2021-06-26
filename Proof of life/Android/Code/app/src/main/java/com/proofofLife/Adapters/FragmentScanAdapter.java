package com.proofofLife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.proofofLife.CustomObjects.CustBluetootDevice;
import com.proofofLife.R;

import java.util.ArrayList;

public class FragmentScanAdapter extends RecyclerView.Adapter<FragmentScanAdapter.ScanItemViewHolder> {
    private ArrayList<CustBluetootDevice> custBluetootDevicesList;
    private Context context;
    private ScanOnItemClickInterface onItemClickInterface;

    public FragmentScanAdapter(ArrayList<CustBluetootDevice> arrayList_loc) {
        this.custBluetootDevicesList = arrayList_loc;
    }

    @NonNull
    @Override
    public ScanItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // View itemView = LayoutInflater.from(context).inflate(R.layout.fragmentscan_scan_item_layout, parent, false);
        View itemView = LayoutInflater.from(context).inflate(R.layout.scan_item_design, parent, false);
        //   View itemView = LayoutInflater.from(context).inflate(R.layout.scan_item_design_test_2, parent, false);
        return new FragmentScanAdapter.ScanItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanItemViewHolder scanItemViewHolder, int position) {
        scanItemViewHolder.bindBluetoothDetails(custBluetootDevicesList.get(position), scanItemViewHolder);
    }

    @Override
    public int getItemCount() {
        return custBluetootDevicesList.size();
    }

    public class ScanItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceAliasName_texView;
        TextView deviceBleAddress_textView;
        AppCompatButton connect_disconnect_button;

        public ScanItemViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceAliasName_texView = (TextView) itemView.findViewById(R.id.deviceName_textView_id);
            deviceBleAddress_textView = (TextView) itemView.findViewById(R.id.device_bleAddress_textView_id);
            connect_disconnect_button = (AppCompatButton) itemView.findViewById(R.id.connect_disconnect_button_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        public void bindBluetoothDetails(CustBluetootDevice custBluetootDevice, ScanItemViewHolder scanItemViewHolder) {
            deviceBleAddress_textView.setText(custBluetootDevice.getBleAddress());
            deviceAliasName_texView.setText(custBluetootDevice.getDeviceName());
            if (custBluetootDevice.isConnected()) {
                connect_disconnect_button.setTextColor(context.getResources().getColor(R.color.disconnect_text_color));
                connect_disconnect_button.setText(context.getResources().getString(R.string.FRAGMET_SCAN_DEVICE_DIS_CONNECT));
            } else if (!custBluetootDevice.isConnected()) {
                connect_disconnect_button.setTextColor(context.getResources().getColor(R.color.connect_text_color));
                connect_disconnect_button.setText(context.getResources().getString(R.string.FRAGMET_SCAN_DEVICE_CONNECT));
            }
            scanItemViewHolder.connect_disconnect_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickInterface != null) {
                        onItemClickInterface.ConnectionStatusClick(custBluetootDevice, getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface ScanOnItemClickInterface {
        public void ConnectionStatusClick(CustBluetootDevice customBluetoothObject, int ItemSlected);
    }

    public void setOnItemClickListner(ScanOnItemClickInterface loc_scanOnItemClickInterface) {
        this.onItemClickInterface = loc_scanOnItemClickInterface;
    }
}


