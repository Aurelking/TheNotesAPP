package com.example.thenotesapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.thenotesapp.NoteDetailsActivity
import com.example.thenotesapp.adapter.NoteAdapter
import com.example.thenotesapp.databinding.FragmentHomeBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodels.NoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment(), NoteAdapter.OnItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(this)
        binding.homeRecyclerView.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupViewModel() {
        noteViewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]
        noteViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            noteAdapter.setNotes(notes)
            binding.emptyNotesImage.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onItemClick(note: Note) {
        val intent = Intent(requireContext(), NoteDetailsActivity::class.java)
        intent.putExtra("note", note)
        startActivity(intent)
    }

    private fun showNoteOptions(note: Note) {
        val options = arrayOf(
            "Modifier",
            if (note.isPinned) "Désépingler" else "Épingler",
            if (note.isArchived) "Désarchiver" else "Archiver",
            "Supprimer"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(note.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> onItemClick(note)
                    1 -> togglePinned(note)
                    2 -> toggleArchived(note)
                    3 -> confirmDeleteNote(note)
                }
            }
            .show()
    }

    private fun togglePinned(note: Note) {
        noteViewModel.updateNote(note.copy(isPinned = !note.isPinned))
    }

    private fun toggleArchived(note: Note) {
        noteViewModel.updateNote(note.copy(isArchived = !note.isArchived))
    }

    private fun confirmDeleteNote(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Supprimer la note")
            .setMessage("Êtes-vous sûr de vouloir supprimer cette note ?")
            .setPositiveButton("Supprimer") { _, _ ->
                noteViewModel.deleteNote(note)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}