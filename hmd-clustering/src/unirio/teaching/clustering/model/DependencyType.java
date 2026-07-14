package unirio.teaching.clustering.model;

/**
 * Tipo de dependencia entre duas classes
 * 
 * @author Marcio Barros
 */
public enum DependencyType
{
	USES ("uses"),
	IMPLEMENTS ("implements"),
	EXTENDS ("extends");

	private final String identifier;

	/**
	 * Inicializa um tipo de dependencia
	 */
	DependencyType(String id)
	{
		this.identifier = id;
	}

	/**
	 * Retorna o identificador do tipo de dependencia
	 */
	public String getIdentifier()
	{
		return identifier;
	}
	
	/**
	 * Retorna um tipo de dependencia, dado um identificado
	 */
	public static DependencyType fromIdentifier(String id)
	{
		for (DependencyType type: DependencyType.values())
			if (type.getIdentifier().compareToIgnoreCase(id) == 0)
				return type;
		
		return null;
	}
}