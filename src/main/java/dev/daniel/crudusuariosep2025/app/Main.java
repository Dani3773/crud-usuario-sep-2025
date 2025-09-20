package dev.daniel.crudusuariosep2025.app;
//menu console com fluxos de criar/listar/atualizar/excluir


import dev.daniel.crudusuariosep2025.domain.Usuario;
import dev.daniel.crudusuariosep2025.repository.UsuarioRepository;
import dev.daniel.crudusuariosep2025.service.UsuarioService;

import java.util.List;
import java.util.Scanner;



public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UsuarioRepository repo = new UsuarioRepository();
        UsuarioService service = new UsuarioService(repo);

        System.out.println("Repo concreto: " + repo.getClass().getName());
        try (var c = dev.daniel.crudusuariosep2025.infra.Database.getConnection();
             var st = c.createStatement();
             var rs = st.executeQuery("SELECT version()")) {
            rs.next();
            System.out.println("DB PING OK: " + rs.getString(1));
        } catch (Exception e) {
            System.out.println("DB PING FALHOU:");
            e.printStackTrace();
        }


        while (true) {
            System.out.println("\n===== CRUD Usuários =====");
            System.out.println("1) Criar usuário");
            System.out.println("2) Listar usuários");
            System.out.println("3) Atualizar usuário");
            System.out.println("4) Excluir usuário");
            System.out.println("5) Sair");
            System.out.print("Escolha: ");
            String opcao = sc.nextLine().trim();

            try {
                switch (opcao) {
                    case "1":
                        criarUsuario(sc, service);
                        break;
                    case "2":
                        listarUsuarios(service);
                        break;
                    case "3":
                        atualizarUsuario(sc, service);
                        break;
                    case "4":
                        excluirUsuario(sc, service);
                        break;
                    case "5":
                        System.out.println("Até logo!");
                        return;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
    }

    private static void criarUsuario(Scanner sc, UsuarioService service) {
        System.out.println("\n-- Criar Usuário --");
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("E-mail: ");
        String email = sc.nextLine();
        System.out.print("Tipo (ADMIN/COMUM): ");
        String tipo = sc.nextLine();

        Usuario criado = service.criar(nome, email, tipo);
        System.out.println("Sucesso: usuário criado -> " + criado);
    }

    private static void listarUsuarios(UsuarioService service) {
        System.out.println("\n-- Listar Usuários --");
        List<Usuario> lista = service.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        lista.forEach(System.out::println);
        System.out.println("Total: " + lista.size());
    }

    private static void atualizarUsuario(Scanner sc, UsuarioService service) {
        System.out.println("\n-- Atualizar Usuário --");
        Integer id = lerInteiro(sc, "ID: ");

        System.out.println("Deixe em branco para manter o valor atual.");
        System.out.print("Novo nome: ");
        String nome = sc.nextLine();
        System.out.print("Novo e-mail: ");
        String email = sc.nextLine();
        System.out.print("Novo tipo (ADMIN/COMUM): ");
        String tipo = sc.nextLine();

        // atualizarUsuario mantém valores antigos se vierem vazios
        Usuario atualizado = service.atualizarUsuario(id, nome, email, tipo);
        System.out.println("Sucesso: usuário atualizado -> " + atualizado);
    }

    private static void excluirUsuario(Scanner sc, UsuarioService service) {
        System.out.println("\n-- Excluir Usuário --");
        Integer id = lerInteiro(sc, "ID: ");
        service.excluirUsuario(id);
        System.out.println("Sucesso: usuário excluído.");
    }

    private static Integer lerInteiro(Scanner sc, String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine();
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID deve ser um número inteiro.");
        }
    }
}
