// Classe de menus
package br.com.CompilaQueVai;
import br.com.CompilaQueVai.services.DistanciaService;
import br.com.CompilaQueVai.services.EstatisticaService;
import br.com.CompilaQueVai.services.TransporteService;

import java.util.Scanner;

public class Menu {
        public void executarMenuPrincipal(){
            Scanner sc = new Scanner(System.in);
            final TransporteService transporteService = new TransporteService();
            final EstatisticaService estatisticaService = new EstatisticaService();
            boolean executando = true;
            while (executando) {
                textoMenuPrincipal();
                int respUsuario = sc.nextInt();
                switch (respUsuario) {
                    case 1:
                        DistanciaService distServ = new DistanciaService();
                        distServ.distanciaEntreCidades();
                        break;
                    case 2:

                        transporteService.cadastrarTransporte();
                        break;
                    case 3:
                        estatisticaService.gerarRelatorios(transporteService.getTransportesCadastrados());
                        break;
                    case 4:
                        executando = false;
                        System.out.println("Encerrando o sistema!");
                        break;
                        // finaliza o programa
                    default:
                        System.out.println("Opção inválida, tente novamente:");
                }
            }
            sc.close();
        }

        public void textoMenuPrincipal(){
            System.out.print("""
                    
                    == Sistema de transporte ==
                    
                    Selecione a opção desejada:             
                    1 - Consultar trechos e modalidades                    
                    2 - Cadastrar transporte                    
                    3 - Dados estatísticos
                    4 - Finalizar programa
                    """);
        }

    }