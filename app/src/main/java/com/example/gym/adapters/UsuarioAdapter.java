package com.example.gym.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.R;
import com.example.gym.models.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> usuarios;
    private OnUsuarioClickListener listener;

    // INTERFAZ DE EVENTOS
    public interface OnUsuarioClickListener {
        void onUsuarioClick(Usuario usuario);
        void onUsuarioLongClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> usuarios, OnUsuarioClickListener listener) {
        this.usuarios = usuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {
        return usuarios != null ? usuarios.size() : 0;
    }

    public void updateUsuarios(List<Usuario> nuevosUsuarios) {
        this.usuarios = nuevosUsuarios;
        notifyDataSetChanged();
    }

    // ---------------------------
    //        VIEW HOLDER
    // ---------------------------
    class UsuarioViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombre;
        private TextView tvApellido;
        private TextView tvRutinasCount;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvApellido = itemView.findViewById(R.id.tvApellido);
            tvRutinasCount = itemView.findViewById(R.id.tvRutinasCount);

            // CLICK NORMAL
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onUsuarioClick(usuarios.get(getAdapterPosition()));
                }
            });

            // CLICK LARGO
            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onUsuarioLongClick(usuarios.get(getAdapterPosition()));
                    return true;  // importante
                }
                return false;
            });
        }

        public void bind(Usuario usuario) {
            tvNombre.setText(usuario.getNombre());
            tvApellido.setText(usuario.getApellido());

            int rutinasCount = usuario.getRutinas() != null
                    ? usuario.getRutinas().size()
                    : 0;

            tvRutinasCount.setText("Rutinas: " + rutinasCount);
        }
    }
}
