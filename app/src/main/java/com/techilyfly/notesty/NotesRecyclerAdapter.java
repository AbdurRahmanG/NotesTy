package com.techilyfly.notesty;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class NotesRecyclerAdapter extends FirestoreRecyclerAdapter <Note, NotesRecyclerAdapter.NoteViewHolder> {

    private static final String TAG = "NoteRecyclerAdapter";


    NoteListener noteListener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NotesRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Note> options, NoteListener noteListener) {
        super(options);

        this.noteListener = noteListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {

        holder.noteTextView.setText(note.getText());
        holder.checkBox.setChecked(note.isCompleted());

        CharSequence dateCharSequence = DateFormat.format("EEEE, MMM d, yyyy h:mm:ss a", note.getCreated().toDate());
        holder.dateTextView.setText(dateCharSequence);

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note_rows, parent, false);

        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTextView, dateTextView;
        CheckBox checkBox;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTextView = itemView.findViewById(R.id.textViewNote);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            checkBox = itemView.findViewById(R.id.checkBox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    Note note = getItem(getAdapterPosition());
                    if (note.isCompleted() != isChecked) {
                        noteListener.handleCheckChanged(isChecked, snapshot);

                    }

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    noteListener.handleEditNote(snapshot);
                }
            });
        }

        public void deleteItem() {

            noteListener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));

        }

    }

    interface NoteListener {
        public void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot);
        public void handleEditNote(DocumentSnapshot snapshot);
        public void handleDeleteItem(DocumentSnapshot snapshot);

    }

}