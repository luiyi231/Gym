package com.example.gym.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gym.R;
import com.example.gym.models.Ejercicio;
import java.util.List;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder> {
    private List<Ejercicio> ejercicios;
    private OnEjercicioClickListener listener; // Nuevo listener

    // Interfaz para manejar clics
    public interface OnEjercicioClickListener {
        void onEjercicioClick(Ejercicio ejercicio);      // Editar
        void onEjercicioLongClick(Ejercicio ejercicio);  // Eliminar
    }

    // Constructor actualizado
    public EjercicioAdapter(List<Ejercicio> ejercicios, OnEjercicioClickListener listener) {
        this.ejercicios = ejercicios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        holder.bind(ejercicios.get(position));
    }

    @Override
    public int getItemCount() {
        return ejercicios != null ? ejercicios.size() : 0;
    }

    public void updateEjercicios(List<Ejercicio> nuevosEjercicios) {
        this.ejercicios = nuevosEjercicios;
        notifyDataSetChanged();
    }

    class EjercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEjercicioNombre, tvReps, tvPeso;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEjercicioNombre = itemView.findViewById(R.id.tvEjercicioNombre);
            tvReps = itemView.findViewById(R.id.tvReps);
            tvPeso = itemView.findViewById(R.id.tvPeso);

            // Configurar Clic Normal (Editar)
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onEjercicioClick(ejercicios.get(getAdapterPosition()));
                }
            });

            // Configurar Clic Largo (Eliminar)
            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onEjercicioLongClick(ejercicios.get(getAdapterPosition()));
                    return true;
                }
                return false;
            });
        }

        public void bind(Ejercicio ejercicio) {
            tvEjercicioNombre.setText(ejercicio.getNombre());
            tvReps.setText("Reps: " + ejercicio.getReps());
            tvPeso.setText("Peso: " + ejercicio.getPeso() + " kg");
        }
    }
}