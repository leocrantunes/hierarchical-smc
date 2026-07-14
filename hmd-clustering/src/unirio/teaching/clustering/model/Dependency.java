package unirio.teaching.clustering.model;

/**
 * Classe que representa uma dependencia entre duas classes
 * 
 * @author Marcio Barros
 */
public class Dependency
{
	private String elementName;
	private DependencyType type;

	/**
	 * Inicializa uma dependencia de uma classe para outra
	 */
	public Dependency(String name, DependencyType type)
	{
		this.elementName = name;
		this.type = type;
	}
	
	/**
	 * Retorna o nome da classe dependida
	 */
	public String getElementName()
	{
		return elementName;
	}
	
	/**
	 * Retorna o tipo da dependencia
	 */
	public DependencyType getType()
	{
		return type;
	}
}