package dto;

import java.util.List;

public class CarrinhoReqDTO {
    private List<CarrinhoProdutoItemDTO> produtos;

    public CarrinhoReqDTO() {
    }

    public CarrinhoReqDTO(String idProduto, int quantidade) {
        this.produtos = List.of(new CarrinhoProdutoItemDTO(idProduto, quantidade));
    }

    public List<CarrinhoProdutoItemDTO> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<CarrinhoProdutoItemDTO> produtos) {
        this.produtos = produtos;
    }
}
