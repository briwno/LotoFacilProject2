import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import model.Apostador;
import model.Apostador.Endereco;

public class Autenticador {
    private static final String USERS_FILE = "src/db/users.json";

    public String autenticarUsuario() {
        return null;
    }

    public void registrarUsuario(Apostador apostador) {

        JSONObject json = new JSONObject();
        json.put("id", apostador.getId());
        json.put("nome", apostador.getNome());
        json.put("email", apostador.getEmail());
        json.put("cpf", apostador.getCpf());
        json.put("telefone", apostador.getTelefone());
        json.put("genero", apostador.getGenero());
        json.put("user", apostador.getUser());
        json.put("senha", apostador.getSenha());
        json.put("dataNascimento", apostador.getDataNascimento());

        JSONObject enderecoJson = new JSONObject();
        Endereco endereco = apostador.getEndereco();
        enderecoJson.put("rua", endereco.getRua());
        enderecoJson.put("bairro", endereco.getBairro());
        enderecoJson.put("cidade", endereco.getCidade());
        enderecoJson.put("estado", endereco.getUF());
        enderecoJson.put("cep", endereco.getCep());

        json.put("endereco", enderecoJson);

        // Adiciona um timestamp ao registro
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        json.put("timestamp", dtf.format(now));

        try {
            // Ler o conteúdo existente do arquivo
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            JSONArray usersArray;

            if (content.isEmpty()) {
                usersArray = new JSONArray();
            } else {
                usersArray = new JSONArray(content);
            }

            // Adicionar o novo usuário à lista
            usersArray.put(json);

            // Escrever a lista atualizada de volta ao arquivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
                writer.write(usersArray.toString(4)); // 4 é a indentação para tornar o JSON legível
                writer.newLine();
                System.out.println("Usuário registrado com sucesso: " + json.toString(4));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int gerarCodigoRecuperacao(){
        System.out.println("Código de recuperação gerado com sucesso");


        int codigo = (int) (Math.random() * 10000);
        return codigo;
    }
}