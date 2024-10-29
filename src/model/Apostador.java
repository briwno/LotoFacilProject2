package model;

public class Apostador {
    private int id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private Endereco endereco;
    private String dataNascimento;
    private String genero;
    private String user;
    private String senha;

    // Getters and Setters

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public class Endereco {
        private String rua;

        private String bairro;
        private String cidade;
        private String estado;
        private int cep;

        // Getters and Setters
        public String getRua() {
            return rua;
        }

        public void setRua(String rua) {
            this.rua = rua;
        }


        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getCidade() {
            return cidade;
        }

        public void setCidade(String cidade) {
            this.cidade = cidade;
        }

        public String getUF() {
            return estado;
        }

        public void setUF(String estado) {
            this.estado = estado;
        }

        public int getCep() {
            return cep;
        }

        public void setCep(int cep) {
            this.cep = cep;
        }
    }
}
