package com.example.mappeinnlevering2s374926;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PersonDetailFragment extends Fragment {

    private Person person;
    private EditText nameEditText, phoneNumberEditText;
    private DatePicker birthdatePicker;
    private Button updateButton, deleteButton, backButton;
    private PersonDataKilde dataKilde;
    private OnPersonActionListener listener;

    // Constructor to pass the selected person
    public PersonDetailFragment(Person person) {
        this.person = person;
    }

    // Interface for communication with MainActivity
    public interface OnPersonActionListener {
        void onPersonUpdated();
        void onPersonDeleted();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnPersonActionListener) {
            listener = (OnPersonActionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnPersonActionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_detail, container, false);

        // Initializes UI elements
        nameEditText = view.findViewById(R.id.nameEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        birthdatePicker = view.findViewById(R.id.birthdatePickerDetail);
        updateButton = view.findViewById(R.id.updatePersonButton);
        deleteButton = view.findViewById(R.id.deletePersonButton);
        backButton = view.findViewById(R.id.goBackButton);

        // Initializes database helper
        dataKilde = new PersonDataKilde(getContext());
        try {
            dataKilde.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sets the current person details
        nameEditText.setText(person.getNavn());
        phoneNumberEditText.setText(person.getTelefonnummer());
        setDatePicker(person.getFoedselsdato());

        // Sets up the update button click listener
        updateButton.setOnClickListener(v -> updatePerson());

        // Sets up the delete button click listener
        deleteButton.setOnClickListener(v -> deletePerson());

        // Sets up the back button click listener
        backButton.setOnClickListener(v -> goBack());

        return view;
    }

    private void setDatePicker(String birthdate) {
        String[] dateParts = birthdate.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);
        birthdatePicker.updateDate(year, month, day);
    }

    // Handles person update
    private void updatePerson() {
        String updatedName = nameEditText.getText().toString();
        String updatedPhoneNumber = phoneNumberEditText.getText().toString();
        int day = birthdatePicker.getDayOfMonth();
        int month = birthdatePicker.getMonth() + 1;
        int year = birthdatePicker.getYear();
        String updatedBirthdate = day + "/" + month + "/" + year;

        // Ensures all fields are filled
        if (!updatedName.isEmpty() && !updatedPhoneNumber.isEmpty()) {
            // Uses the person's ID to update the database
            int rowsUpdated = dataKilde.oppdaterPerson(person.getId(), updatedName, updatedPhoneNumber, updatedBirthdate);
            if (rowsUpdated > 0) {
                Toast.makeText(getActivity(), "Person oppdatert!", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onPersonUpdated();
                }
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getActivity(), "Person ble ikke lagret!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Vennligst fyll inn alle feltene.", Toast.LENGTH_SHORT).show();
        }
    }

    // Handles person deletion
    private void deletePerson() {
        dataKilde.slettPerson(person.getId());
        Toast.makeText(getActivity(), "Person slettet!", Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onPersonDeleted();
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }

    // Handles going back without saving
    private void goBack() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataKilde.close();
    }
}