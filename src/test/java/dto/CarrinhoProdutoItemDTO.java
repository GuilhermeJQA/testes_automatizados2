package dto;

public class CarrinhoProdutoItemDTO {
    private String idProduto;
    private int quantidade;

    public CarrinhoProdutoItemDTO() {
    }

    public CarrinhoProdutoItemDTO(String idProduto, int quantidade) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
