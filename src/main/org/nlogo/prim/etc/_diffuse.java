package org.nlogo.prim.etc ;

import org.nlogo.api.AgentException;
import org.nlogo.api.Dump;
import org.nlogo.agent.PatchException;
import org.nlogo.api.I18N;
import org.nlogo.api.LogoException;
import org.nlogo.nvm.Command;
import org.nlogo.nvm.Context;
import org.nlogo.nvm.EngineException;
import org.nlogo.nvm.Syntax;

public final strictfp class _diffuse
	extends Command
{
	@Override
	public Syntax syntax()
	{
		return Syntax.commandSyntax
			( new int[] { Syntax.TYPE_REFERENCE , Syntax.TYPE_NUMBER } ,
			  "O---" , true ) ;
	}
	@Override
	public String toString()
	{
		if( reference != null && world != null )
		{
			return super.toString() + ":" + world.patchesOwnNameAt( reference.vn() ) ;
		}
		else
		{
			return super.toString() ;
		}
	}
	@Override
	public void perform( final Context context ) throws LogoException
	{
		double diffuseparam = argEvalDoubleValue( context , 0 ) ;
		if( diffuseparam < 0.0 || diffuseparam > 1.0 )
		{
            throw new EngineException
				( context , this , I18N.errors().getNJava("org.nlogo.prim.$common.paramOutOfBounds",
                        new String [] {new Double(diffuseparam).toString()})) ;
		}
		try
	    {
			world.diffuse( diffuseparam , reference.vn() ) ;
		}
		catch ( AgentException ex )
		{
			throw new EngineException( context , this , ex.getMessage() ) ;
		}
		catch ( PatchException ex )
		{
			Object value = ex.patch().getPatchVariable( reference.vn() ) ;
			throw new EngineException
				( context , this , 
				  ex.patch() + " should contain a number in the " + world.patchesOwnNameAt( reference.vn() ) +
				  " variable, but contains " +
				  ( value instanceof org.nlogo.api.Nobody
					? "NOBODY"
					: "the " + Syntax.typeName(value) + " " + Dump.logoObject( value ) ) +
				  " instead" ) ;
		}
		context.ip = next ;
	}
}
