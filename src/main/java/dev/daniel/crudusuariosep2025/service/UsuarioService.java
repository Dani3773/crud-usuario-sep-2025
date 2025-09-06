package dev.daniel.crudusuariosep2025.service;


//service com validações de negócio e mensagens de erro

import dev.daniel.crudusuariosep2025.domain.Usuario;
import dev.daniel.crudusuariosep2025.repository.UsuarioRepository;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.Locale;


public class UsuarioService {

    private final UsuarioRepository repository;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public UsuarioService(UsuarioRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String normalizarTipo(String tipo) {
        return tipo.trim().toUpperCase(Locale.ROOT);
    }

    private void validarEmailFormato(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("E-mail em formato inválido.");
        }
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }


    private void validarTipo(String tipo) {
        String t = normalizarTipo(tipo);
        if (!t.equals("ADMIN") && !t.equals("COMUM")) {
            throw new IllegalArgumentException("Tipo inválido. Use ADMIN ou COMUM.");
        }
    }

    public Usuario criar(String nome, String email, String tipo) {
        if (isBlank(nome)) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (isBlank(email)) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }
        if (isBlank(tipo)) {
            throw new IllegalArgumentException("Tipo é obrigatório (ADMIN ou COMUM).");
        }

        validarEmailFormato(email);
        validarTipo(tipo);

        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        Usuario novo = new Usuario(null, nome.trim(), email.trim().toLowerCase(Locale.ROOT), normalizarTipo(tipo));
        return repository.save(novo);
    }

    public Usuario buscarPorId(Integer id) {
        if (id == null) throw new IllegalArgumentException("ID é obrigatório.");
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com id " + id + " não encontrado."));
    }

    // ... existing code ...
    public Usuario atualizarUsuario(Integer id, String nome, String email, String tipo) {
        if (id == null) {
            throw new IllegalArgumentException("ID é obrigatório para atualização.");
        }

        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com id " + id + " não encontrado."));

        // mantém antigos quando vier vazio/null
        String novoNome = (nome != null && !nome.trim().isEmpty()) ? nome : existente.getNome();
        String novoEmail = (email != null && !email.trim().isEmpty()) ? email : existente.getEmail();
        String novoTipo = (tipo != null && !tipo.trim().isEmpty()) ? tipo : existente.getTipo();

        if (isBlank(novoNome)) throw new IllegalArgumentException("Nome é obrigatório.");
        if (isBlank(novoEmail)) throw new IllegalArgumentException("E-mail é obrigatório.");
        if (isBlank(novoTipo)) throw new IllegalArgumentException("Tipo é obrigatório (ADMIN ou COMUM).");

        validarEmailFormato(novoEmail);
        validarTipo(novoTipo);

        repository.findByEmail(novoEmail)
                .filter(u -> !u.getId().equals(id))
                .ifPresent(u -> {
                    throw new IllegalArgumentException("E-mail já cadastrado por outro usuário.");
                });

        existente.setNome(novoNome.trim());
        existente.setEmail(novoEmail.trim().toLowerCase(Locale.ROOT));
        existente.setTipo(normalizarTipo(novoTipo));

        return repository.save(existente);
    }

    // ... existing code ...
    public void excluirUsuario(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID é obrigatório para exclusão.");
        }
        boolean removido = repository.deleteById(id);
        if (!removido) {
            throw new IllegalArgumentException("Usuário com id " + id + " não encontrado.");
        }
    }
}