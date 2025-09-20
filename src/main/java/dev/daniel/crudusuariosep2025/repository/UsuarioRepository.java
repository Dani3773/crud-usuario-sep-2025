package dev.daniel.crudusuariosep2025.repository;

import dev.daniel.crudusuariosep2025.domain.Usuario;
import dev.daniel.crudusuariosep2025.infra.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private String normalizarEmail(String email) {
        if (email == null) return null;
        String t = email.trim();
        return t.isEmpty() ? null : t.toLowerCase();
    }

    public Usuario save(Usuario u) {
        if (u.getId() == null) {
            final String sql = "INSERT INTO usuarios (nome, email, tipo) VALUES (?, ?, ?) RETURNING id";
            try (Connection c = Database.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, u.getNome());
                ps.setString(2, normalizarEmail(u.getEmail()));
                ps.setString(3, u.getTipo());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) u.setId(rs.getInt("id"));
                }
                return u;
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) throw new RuntimeException("E-mail já existe (unique).", e);
                throw new RuntimeException("Erro ao inserir usuário.", e);
            }
        } else {
            final String sql = "UPDATE usuarios SET nome = ?, email = ?, tipo = ? WHERE id = ?";
            try (Connection c = Database.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, u.getNome());
                ps.setString(2, normalizarEmail(u.getEmail()));
                ps.setString(3, u.getTipo());
                ps.setInt(4, u.getId());
                int n = ps.executeUpdate();
                if (n == 0) throw new RuntimeException("Usuário não encontrado. ID=" + u.getId());
                return u;
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) throw new RuntimeException("E-mail já existe (unique).", e);
                throw new RuntimeException("Erro ao atualizar usuário.", e);
            }
        }
    }

    public Optional<Usuario> findById(Integer id) {
        final String sql = "SELECT id,nome,email,tipo FROM usuarios WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por id.", e);
        }
    }

    public List<Usuario> findAll() {
        final String sql = "SELECT id,nome,email,tipo FROM usuarios ORDER BY id";
        List<Usuario> out = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários.", e);
        }
    }

    public boolean deleteById(Integer id) {
        final String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuário.", e);
        }
    }

    public boolean existsByEmail(String email) {
        final String sql = "SELECT 1 FROM usuarios WHERE email = ? LIMIT 1";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, normalizarEmail(email));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao checar e-mail.", e);
        }
    }

    public Optional<Usuario> findByEmail(String email) {
        final String sql = "SELECT id,nome,email,tipo FROM usuarios WHERE email = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, normalizarEmail(email));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por e-mail.", e);
        }
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setTipo(rs.getString("tipo"));
        return u;
    }
}
