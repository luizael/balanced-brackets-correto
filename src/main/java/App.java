import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    private static Map<Integer, Brackets> template = new HashMap<Integer, Brackets>();
    final private static List<Brackets> bracketsFound = new ArrayList<>();
    private static Brackets smallBrackets;

    public static void main(String[] args) {
        String[] valor = {
                "(",
                "{",
                "{",
                "[",
                ")",
                "[",
                "]",
                "]",
                "]",
                "}",
                "}"
        };
        balancedBrackers(valor);
    }

    public static void balancedBrackers(String[] valor){
        System.out.println("***BRAVI - BALANCED BRACKETS***");
        System.out.println("MONTANDO TEMPLATE...");

        buildTemplate();

        System.out.println("VALIDANDO BRACKETS...");
        Arrays.asList(valor)
                .forEach(arg -> {
                    validateBracketsInTemplate(arg);
        });
        System.out.println("ORDENANDO BRACKETS...");
        orderByPairsBrackets();
        removeAloneBrackets();
        printBrackets();
    }

    private static void validateBracketsInTemplate(final String valor){
        Optional<Brackets> result = template.entrySet()
                .stream()
                .filter( template -> valor.equals(template.getValue().getValor()))
                .map( temp -> temp.getValue())
                .findFirst();
        if(result.isPresent()) {
            addBracketsFound(result.get());
        }else{
            System.out.println("VALOR INVÁLIDO...");
        }
    }

    public static void addBracketsFound(final Brackets brackets){
        if(bracketsFound.size() == 0) {
            bracketsFound.add(brackets);
            return;
        }
        if( brackets.getTipo() == Brackets.Tipo.PARENTESES){
            filterMatchBracketsForType(brackets, Brackets.Tipo.PARENTESES);
        }
        if( brackets.getTipo() == Brackets.Tipo.CHAVES){
            filterMatchBracketsForType(brackets, Brackets.Tipo.CHAVES);
        }
        if( brackets.getTipo() == Brackets.Tipo.COLCHETES){
            filterMatchBracketsForType(brackets, Brackets.Tipo.COLCHETES);
        }
    }

    private static void filterMatchBracketsForType(Brackets brackets, Brackets.Tipo tipo){
        if( brackets.getTipo() == tipo){
            Optional<Brackets> result = bracketsFound
                    .stream()
                    .filter( valor -> valor.getTipo().equals(tipo))
                    .findFirst();
            if(result.isPresent()){
                validateMatchBrackets(result.get(), brackets);
            }else {
                validadeLastBracketsGroup(brackets);
            }
        }
    }

    private static void validateMatchBrackets(Brackets resultBrackets, Brackets newBrackets){
        if(resultBrackets.getId() != newBrackets.getId()){
            if(resultBrackets.getId() < newBrackets.getId() ){
                bracketsFound.add(newBrackets);
                return;
            }else{
                System.out.println("SEQUENCIA ERRADA...");
                return;
            }
        }
    }

    private static void validadeLastBracketsGroup(Brackets brackets){
        Map<Integer, Brackets> result =  template.entrySet()
                .stream()
                .filter( valor -> valor.getValue().getTipo().equals(brackets.getTipo()))
                .collect(Collectors.toMap(x ->x.getKey(), y -> y.getValue()));

        result.entrySet().forEach(value ->{
            if(smallBrackets == null){
                smallBrackets = value.getValue();
            }else{
                if(smallBrackets.getId() < value.getValue().getId()){
                    if(smallBrackets.getId() == brackets.getId()){
                        bracketsFound.add(brackets);
                    }
                }
            }
        });
        smallBrackets = null;
    }

    private static void orderByPairsBrackets(){
        bracketsFound.sort((a,b) -> a.getTipo().compareTo(b.getTipo()));
    }
    private static void removeAloneBrackets(){
        template.entrySet().stream().forEach( bracketsTemplate ->{
           List<Brackets> result = bracketsFound.stream().filter(bracketsFound -> bracketsFound.getTipo().equals(bracketsTemplate.getValue().getTipo())).collect(Collectors.toList());
           if(result.size() < 2){
               try {
                   bracketsFound.remove(result.get(0));
               }catch (Exception e){
                   System.out.print("ERRO AO REMOVER PARENTES ALONE:");
               }
           }else if( result.size() > 2 ){
               result.forEach( brackets ->{
                   if(smallBrackets == null){
                       smallBrackets = brackets;
                   }else{
                       if(smallBrackets.getId() == brackets.getId() ){
                           try {
                               bracketsFound.remove(brackets);
                           }catch (Exception e){
                               System.out.print("ERRO AO REMOVER PARENTES ALONE:");
                           }
                       }
                       smallBrackets = brackets;
                   }
               });
               smallBrackets = null;
           }
        });
    }
    private static void printBrackets(){
        System.out.println("TOTAL ENCONTRADO:" + bracketsFound.size());
        System.out.print("RESULTADO:");
        if(bracketsFound.size() > 0)
            bracketsFound.forEach(brackets -> System.out.print(brackets.getValor()));
    }

    private static void buildTemplate(){
        template.put(0, new Brackets(0,"(", Brackets.Tipo.PARENTESES));
        template.put(1, new Brackets(1,")",Brackets.Tipo.PARENTESES));
        template.put(2, new Brackets(2,"{", Brackets.Tipo.CHAVES));
        template.put(3,new Brackets(3,"}",Brackets.Tipo.CHAVES));
        template.put(4, new Brackets(4,"[",Brackets.Tipo.COLCHETES));
        template.put(5, new Brackets(5,"]",Brackets.Tipo.COLCHETES));
    }
}


class Brackets implements Serializable {

    private Integer id;
    private String valor;
    private Tipo tipo;
    enum Tipo{PARENTESES, COLCHETES, CHAVES}

    public Brackets(Integer id, String valor, Tipo tipo) {
        this.id = id;
        this.valor = valor;
        this.tipo = tipo;
    }

    public Integer getId() {
        return id;
    }
    public String getValor() {
        return valor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brackets brackets = (Brackets) o;
        return Objects.equals(id, brackets.id) &&
                Objects.equals(valor, brackets.valor) &&
                tipo == brackets.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, valor, tipo);
    }
}

