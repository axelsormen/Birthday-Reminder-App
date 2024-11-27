package com.example.mappeinnlevering2s374926;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    private final List<Person> personer;
    private final OnItemClickListener listener;

    public PersonAdapter(List<Person> personer, OnItemClickListener listener) {
        this.personer = personer;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Person person = personer.get(position);
        holder.bind(person, listener);
    }

    @Override
    public int getItemCount() {
        return personer.size();
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        private final TextView navnTextView;
        private final TextView telefonnummerTextView;
        private final TextView foedselsdatoTextView;

        public PersonViewHolder(View itemView) {
            super(itemView);
            navnTextView = itemView.findViewById(R.id.nameTextView);
            telefonnummerTextView = itemView.findViewById(R.id.phoneNumberTextView);
            foedselsdatoTextView = itemView.findViewById(R.id.birthdateTextView);
        }

        public void bind(final Person person, final OnItemClickListener listener) {
            navnTextView.setText(person.getNavn());
            telefonnummerTextView.setText("Telefonnummer: "+person.getTelefonnummer());
            foedselsdatoTextView.setText("Fødselsdato: "+person.getFoedselsdato());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(person);
                }
            });
        }
    }

    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Person person);
    }
}
