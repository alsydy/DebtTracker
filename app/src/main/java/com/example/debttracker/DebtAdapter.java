package com.example.debttracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.util.List;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {
    private List<Debt> debts;
    private Context context;
    private OnDebtClickListener listener;
    private DecimalFormat decimalFormat;

    public interface OnDebtClickListener {
        void onDebtClick(Debt debt);
        void onDebtLongClick(Debt debt);
    }

    public DebtAdapter(Context context, List<Debt> debts) {
        this.context = context;
        this.debts = debts;
        this.decimalFormat = new DecimalFormat("#0.00");
    }

    public void setOnDebtClickListener(OnDebtClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DebtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_debt, parent, false);
        return new DebtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DebtViewHolder holder, int position) {
        Debt debt = debts.get(position);
        
        holder.tvPersonName.setText(debt.getPersonName());
        holder.tvAmount.setText(decimalFormat.format(debt.getAmount()));
        holder.tvDescription.setText(debt.getDescription());
        holder.tvDate.setText(debt.getDate());
        
        // Set amount color based on debt type
        if (debt.isOwedToMe()) {
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
        
        // Set status
        if (debt.isPaid()) {
            holder.tvStatus.setText(R.string.paid);
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.tvStatus.setText(R.string.unpaid);
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return debts.size();
    }

    public void updateDebts(List<Debt> newDebts) {
        this.debts = newDebts;
        notifyDataSetChanged();
    }

    class DebtViewHolder extends RecyclerView.ViewHolder {
        TextView tvPersonName, tvAmount, tvDescription, tvDate, tvStatus;

        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPersonName = itemView.findViewById(R.id.tv_person_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDebtClick(debts.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDebtLongClick(debts.get(getAdapterPosition()));
                    return true;
                }
                return false;
            });
        }
    }
}

