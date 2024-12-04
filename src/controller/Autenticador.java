import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.Alert;
import model.Aposta;
import model.Apostador;
import model.Apostador.Endereco;
import model.Concurso;

public class Autenticador {
    private static final String USERS_FILE = "src/db/users.json";
    private static final String CONCURSOS_FILE = "src/db/concursos.json";
    private String usuarioLogado;
    private String idLogado;

    public StringBuilder autenticarUsuario(String user, String senha) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            JSONArray usersArray = new JSONArray(content);

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject usuario = usersArray.getJSONObject(i);
                if (usuario.getString("user").equals(user) && usuario.getString("senha").equals(senha)) {
                    String usuarioLogado = usuario.getString("nome");
                    int idLogado = usuario.getInt("id");
                    System.out.println("Usuário autenticado com sucesso: " + usuarioLogado);
                    StringBuilder sb = new StringBuilder();
                    sb.append(usuarioLogado);
                    sb.append(";");
                    sb.append(idLogado);
                    return sb;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    
    public int getNextId() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            JSONArray usersArray = new JSONArray(content);

            int maxId = 0;
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject usuario = usersArray.getJSONObject(i);
                int id = usuario.getInt("id");
                if (id > maxId) {
                    maxId = id;
                }
            }

            return maxId + 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        
    }

    public static void salvarConcurso(Concurso concurso){
        final String CONCURSOS_FILE = "src/db/concursos.json";

        JSONObject json = new JSONObject();
        json.put("id", concurso.getId());
        json.put("dataCriacao", concurso.getDataCriacao());
        json.put("horario", concurso.getHorario());
        json.put("dataSorteio", concurso.getDataSorteio());
        json.put("numerosSorteados", concurso.getNumerosSorteados());
        json.put("situacao", concurso.getSituacao());
        json.put("apostas", concurso.getApostas());
        json.put("premioAcumulado", concurso.getPremioAcumulado());
        

        try{
            String content = new String(Files.readAllBytes(Paths.get(CONCURSOS_FILE)));
            JSONArray concursosArray;

            if(content.isEmpty()){
                concursosArray = new JSONArray();
            } else {
                concursosArray = new JSONArray(content);
            }

            concursosArray.put(json);

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(CONCURSOS_FILE))){
                writer.write(concursosArray.toString(4));
                writer.newLine();
                System.out.println("Concurso salvo com sucesso: " + json.toString(4));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static JSONArray carregarConcursos() {
        try {
            return new JSONArray(new String(Files.readAllBytes(Paths.get(CONCURSOS_FILE)), StandardCharsets.UTF_8));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao carregar dados");
            alert.setHeaderText("Não foi possível carregar os dados dos concursos.");
            alert.setContentText("Verifique se o arquivo 'concursos.json' está acessível.");
            alert.showAndWait();
            return new JSONArray();
        }
    }

    public static JSONArray carregarApostas() {
        try {
            return new JSONArray(new String(Files.readAllBytes(Paths.get("src/db/apostas.json")), StandardCharsets.UTF_8));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao carregar dados");
            alert.setHeaderText("Não foi possível carregar os dados das apostas.");
            alert.setContentText("Verifique se o arquivo 'apostas.json' está acessível.");
            alert.showAndWait();
            return new JSONArray();
        }
    }

    public static JSONArray carregarUsuarios() {
        try {
            return new JSONArray(new String(Files.readAllBytes(Paths.get(USERS_FILE)), StandardCharsets.UTF_8));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao carregar dados");
            alert.setHeaderText("Não foi possível carregar os dados dos usuários.");
            alert.setContentText("Verifique se o arquivo 'users.json' está acessível.");
            alert.showAndWait();
            return new JSONArray();
        }
    }

    public static void salvarAposta(Aposta aposta){
        final String APOSTAS_FILE = "src/db/apostas.json";

        JSONObject json = new JSONObject();
        json.put("idAposta", aposta.getIdAposta());
        json.put("idConcurso", aposta.getIdConcurso());
        json.put("valorPago", aposta.getValorPago());
        json.put("dataCriacao", aposta.getDataCriacao());
        json.put("numerosSelecionados", aposta.getNumerosSelecionados());
        json.put("qtdeAcertos", aposta.getQtdeAcertos());
        json.put("idApostador", aposta.getIdApostador());
        json.put("valorGanho", aposta.getValorGanho());

        try{
            String content = new String(Files.readAllBytes(Paths.get(APOSTAS_FILE)));
            JSONArray apostasArray;

            if(content.isEmpty()){
                apostasArray = new JSONArray();
            } else {
                apostasArray = new JSONArray(content);
            }

            apostasArray.put(json);

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(APOSTAS_FILE))){
                writer.write(apostasArray.toString(4));
                writer.newLine();
                System.out.println("Aposta salva com sucesso: " + json.toString(4));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        
        
    }

    

    public boolean cpfJaRegistrado(String cpf){
        try{
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            JSONArray usersArray = new JSONArray(content);

            for(int i = 0; i < usersArray.length(); i++){
                JSONObject usuario = usersArray.getJSONObject(i);
                if(usuario.getString("cpf").equals(cpf)){
                    System.out.println("CPF já cadastrado");
                    return true;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    

    public boolean usuarioJaRegistrado(String user){
        try{
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            JSONArray usersArray = new JSONArray(content);

            for(int i = 0; i < usersArray.length(); i++){
                JSONObject usuario = usersArray.getJSONObject(i);
                if(usuario.getString("user").equals(user)){
                    System.out.println("Usuário já cadastrado");
                    return true;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public String gerarCodigoRecuperacao() {
        System.out.println("Código de recuperação gerado com sucesso");

        int codigo = (int) (Math.random() * 10000);
        String codigoStr = String.format("%04d", codigo);
        return codigoStr;
    }

    public boolean verificarEmailCadastrado(String email) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(USERS_FILE)));
            JSONArray usersArray = new JSONArray(content);

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("email").equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }



    public int calcularIdade(LocalDate dataNascimento) {
        LocalDate dataAtual = LocalDate.now();
        int idade = dataAtual.getYear() - dataNascimento.getYear();
        if (dataAtual.getMonthValue() < dataNascimento.getMonthValue()
                || (dataAtual.getMonthValue() == dataNascimento.getMonthValue()
                        && dataAtual.getDayOfMonth() < dataNascimento.getDayOfMonth())) {
            idade--;
        }
        return idade;
    }
}