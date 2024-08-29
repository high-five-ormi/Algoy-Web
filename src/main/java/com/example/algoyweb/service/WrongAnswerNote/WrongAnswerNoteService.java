package com.example.algoyweb.service.WrongAnswerNote;

import com.example.algoyweb.model.dto.WrongAnswerNote.WrongAnswerNoteDTO;
import com.example.algoyweb.model.entity.WrongAnswerNote.WrongAnswerNote;
import com.example.algoyweb.repository.WrongAnswerNote.WrongAnswerNoteRepository;
import com.example.algoyweb.util.WrongAnswerNote.WrongAnswerNoteConvertUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WrongAnswerNoteService {

    private final WrongAnswerNoteRepository repository;

    public WrongAnswerNoteService(WrongAnswerNoteRepository repository) {
        this.repository = repository;
    }

    public List<WrongAnswerNoteDTO> findAll() {
        return repository.findAll().stream()
            .map(WrongAnswerNoteConvertUtil::convertToDto)
            .collect(Collectors.toList());
    }

    public Optional<WrongAnswerNoteDTO> findById(Long id) {
        return repository.findById(id)
            .map(WrongAnswerNoteConvertUtil::convertToDto);
    }

    public void save(WrongAnswerNoteDTO dto) {
        WrongAnswerNote entity = WrongAnswerNoteConvertUtil.convertToEntity(dto);
        repository.save(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}