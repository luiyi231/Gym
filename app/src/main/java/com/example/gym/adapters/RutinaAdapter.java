package com.example.gym.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gym.R;
import com.example.gym.models.Rutina;
import java.util.List;

public class RutinaAdapter extends RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder> {
    private List<Rutina> rutinas;
    private OnRutinaClickListener listener;

    public interface OnRutinaClickListener {
        void onRutinaClick(Rutina rutina);       // Ver detalles (o editar)
        void onRutinaLongClick(Rutina rutina);   // Eliminar
    }

    public RutinaAdapter(List<Rutina> rutinas, OnRutinaClickListener listener) {
        this.rutinas = rutinas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rutina, parent, false);
        return new RutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        holder.bind(rutinas.get(position));
    }

    @Override
    public int getItemCount() { return rutinas != null ? rutinas.size() : 0; }

    public void updateRutinas(List<Rutina> nuevasRutinas) {
        this.rutinas = nuevasRutinas;
        notifyDataSetChanged();
    }

    class RutinaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRutinaNombre, tvEjerciciosCount, tvUsuarioId;

        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRutinaNombre = itemView.findViewById(R.id.tvRutinaNombre);
            tvEjerciciosCount = itemView.findViewById(R.id.tvEjerciciosCount);
            tvUsuarioId = itemView.findViewById(R.id.tvUsuarioId);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onRutinaClick(rutinas.get(getAdapterPosition()));
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onRutinaLongClick(rutinas.get(getAdapterPosition()));
                    return true;
                }
                return false;
            });
        }

        public void bind(Rutina rutina) {
            tvRutinaNombre.setText(rutina.getRutinaNombre());
            int count = rutina.getEjercicios() != null ? rutina.getEjercicios().size() : 0;
            tvEjerciciosCount.setText("Ejercicios: " + count);
            tvUsuarioId.setText("Usuario ID: " + rutina.getUsuarioId());
        }
    }
}