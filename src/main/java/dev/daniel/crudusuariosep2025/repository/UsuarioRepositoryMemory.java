package dev.daniel.crudusuariosep2025.repository;

//repositório em memória para Usuario (CRUD básico + gerador de id)

import dev.daniel.crudusuariosep2025.domain.Usuario;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UsuarioRepositoryMemory {

    private final Map<Integer, Usuario> storage = new LinkedHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(0);
    private final Map<String, Integer> emailIndex = new HashMap<>();

    private String normalizarEmail(String email) {
        if (email == null) return null;
        String trimmed = email.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase(Locale.ROOT);
    };


    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            int novoId = sequence.incrementAndGet();
            usuario.setId(novoId);
        }
            Usuario antigo = storage.get(usuario.getId());
        if (antigo != null) {
            String emailAntigo = normalizarEmail(antigo.getEmail());
            String emailNovo = normalizarEmail(usuario.getEmail());
            if (!Objects.equals(emailAntigo, emailNovo)) {
                if (emailAntigo != null) {
                    emailIndex.remove(emailAntigo);
                }
                if (emailNovo != null) {
                    emailIndex.put(emailNovo, usuario.getId());
                }
            }
        } else {
            String emailNovo = normalizarEmail(usuario.getEmail());
            if (emailNovo != null) {
                emailIndex.put(emailNovo, usuario.getId());
            }
        }
        storage.put(usuario.getId(), usuario);
        return usuario;
        }


    public Optional<Usuario> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
        }
        public List<Usuario> findAll() {
        return new ArrayList<>(storage.values());
        }
        public boolean deleteById(Integer id) {
            Usuario removido = storage.remove(id);
            if (removido != null) {
                String email = normalizarEmail(removido.getEmail());
                if (email != null) {
                    emailIndex.remove(email);
                }
                return true;
            }
            return false;
        }
        public boolean existsByEmail(String email) {
            String key = normalizarEmail(email);
            if (key == null) return false;
            return emailIndex.containsKey(key);
        }

    public Optional<Usuario> findByEmail(String email) {
        String key = normalizarEmail(email);
        if (key == null) return Optional.empty();
        Integer id = emailIndex.get(key);
        return id != null ? Optional.ofNullable(storage.get(id)) : Optional.empty();
    }




}

