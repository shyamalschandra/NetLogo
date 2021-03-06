// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim;

import org.nlogo.core.AgentKindJ;
import org.nlogo.agent.AgentSet;
import org.nlogo.agent.ArrayAgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.api.LogoException;
import org.nlogo.core.Syntax;
import org.nlogo.nvm.Command;
import org.nlogo.nvm.Context;

public final strictfp class _createorderedturtles
    extends Command
    implements org.nlogo.nvm.CustomAssembled {
  static final String NO_BREED = "";
  public final String breedName;

  public _createorderedturtles() {
    breedName = NO_BREED;
    this.switches = true;
  }

  public _createorderedturtles(String breedName) {
    this.breedName = breedName;
    this.switches = true;
  }



  @Override
  public String toString() {
    return super.toString() + ":" + breedName + ",+" + offset;
  }

  @Override
  public void perform(final Context context)
      throws LogoException {
    int numberOfTurtles = argEvalIntValue(context, 0);
    if (numberOfTurtles > 0) {
      AgentSet agentset =
          new ArrayAgentSet(AgentKindJ.Turtle(), numberOfTurtles, false);
      AgentSet breed =
          breedName == NO_BREED
              ? world.turtles()
              : world.getBreed(breedName);
      for (int i = 0; i < numberOfTurtles; i++) {
        Turtle turtle = world.createTurtle(breed);
        turtle.colorDouble(Double.valueOf(10.0 * i + 5.0));
        turtle.heading((360.0 * i) / numberOfTurtles);
        agentset.add(turtle);
        workspace.joinForeverButtons(turtle);
      }
      context.runExclusiveJob(agentset, next);
    }
    context.ip = offset;
  }

  public void assemble(org.nlogo.nvm.AssemblerAssistant a) {
    a.add(this);
    a.block();
    a.done();
    a.resume();
  }
}
