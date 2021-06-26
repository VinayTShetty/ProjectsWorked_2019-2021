package com.proofofLife.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.proofofLife.CustomObjects.CustBluetootDevice;
import com.proofofLife.R;
import java.util.ArrayList;
public class FragmentAcccountsAdapter extends RecyclerView.Adapter<FragmentAcccountsAdapter.AccountItemHolder> {
    private ArrayList<CustBluetootDevice> custBluetootDevicesList;
    private Context context;
    private AccountsItemInterface accountsItemInterface;

    public FragmentAcccountsAdapter(ArrayList<CustBluetootDevice> arrayList_loc) {
        this.custBluetootDevicesList = arrayList_loc;
    }

    @NonNull
    @Override
    public AccountItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.account_item_design, parent, false);
        return new FragmentAcccountsAdapter.AccountItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountItemHolder accountItemHolder, int position) {
        accountItemHolder.bindBluetoothDetails(custBluetootDevicesList.get(position), accountItemHolder);
    }

    @Override
    public int getItemCount() {
        return custBluetootDevicesList.size();
    }

    public class AccountItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView device_bleAddress_textView;
        TextView device_name_textView;

        public AccountItemHolder(@NonNull View itemView) {
            super(itemView);
            device_bleAddress_textView = (TextView) itemView.findViewById(R.id.device_bleAddress_textView_id);
            device_name_textView = (TextView) itemView.findViewById(R.id.deviceName_textView_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (accountsItemInterface != null) {
                accountsItemInterface.accountsItemClick(custBluetootDevicesList.get(getAdapterPosition()), getAdapterPosition());
            }

        }

        public void bindBluetoothDetails(CustBluetootDevice custBluetootDevice, AccountItemHolder accountItemHolder) {
            device_bleAddress_textView.setText(custBluetootDevice.getBleAddress());
            device_name_textView.setText("Token "+custBluetootDevice.getId());
        }
    }

    public interface AccountsItemInterface {
        public void accountsItemClick(CustBluetootDevice customBluetoothObject, int ItemSlected);
    }

    public void setupOnItemClicklistnerInterface(AccountsItemInterface accountsItemInterface_loc) {
        this.accountsItemInterface = accountsItemInterface_loc;
    }
}
