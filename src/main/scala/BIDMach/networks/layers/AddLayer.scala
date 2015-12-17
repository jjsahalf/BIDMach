package BIDMach.networks.layers

import BIDMat.{Mat,SBMat,CMat,DMat,FMat,IMat,LMat,HMat,GMat,GDMat,GIMat,GLMat,GSMat,GSDMat,SMat,SDMat}
import BIDMat.MatFunctions._
import BIDMat.SciFunctions._
import BIDMach.datasources._
import BIDMach.updaters._
import BIDMach.mixins._
import BIDMach.models._
import BIDMach._
import edu.berkeley.bid.CPUMACH
import edu.berkeley.bid.CUMACH
import scala.util.hashing.MurmurHash3;
import java.util.HashMap;
import BIDMach.networks._


/**
 * Computes the sum of input layers. 
 */

class AddLayer(override val net:Net, override val opts:AddNodeOpts = new AddNode) extends Layer(net, opts) { 
  
  override val _inputs = new Array[Layer](opts.ninputs);
  override val _inputTerminals = new Array[Int](opts.ninputs);

	override def forward = {
      val start = toc;
			createoutput(inputData.nrows, inputData.ncols);
			output <-- inputData;
			(1 until inputlength).map((i:Int) => output ~ output + inputDatas(i));
			clearDeriv;
			forwardtime += toc - start;
	}

	override def backward = {
      val start = toc;
			(0 until inputlength).map((i:Int) => {
				if (inputDerivs(i).asInstanceOf[AnyRef] != null) inputDerivs(i) ~ inputDerivs(i) + deriv
			});
			backwardtime += toc - start;
	}
}

trait AddNodeOpts extends NodeOpts {
	var ninputs = 2;
	override val inputs:Array[NodeOpts] = new Array[NodeOpts](ninputs);
	override val inputTerminals:Array[Int] = new Array[Int](ninputs);
	
	def copyTo(opts:AddNodeOpts):AddNodeOpts = {
			super.copyTo(opts);
			opts.ninputs = ninputs;
			opts;
	}
}

class AddNode extends Node with AddNodeOpts {

	override def clone:AddNode = {copyTo(new AddNode).asInstanceOf[AddNode];}

	override def create(net:Net):AddLayer = {AddLayer(net, this);}
}

object AddLayer { 
  
  def apply(net:Net) = new AddLayer(net, new AddNode);
  
  def apply(net:Net, opts:AddNodeOpts) = new AddLayer(net, opts); 
}