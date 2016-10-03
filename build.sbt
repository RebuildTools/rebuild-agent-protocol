import com.trueaccord.scalapb.{ScalaPbPlugin => PB}
import java.io.File

name := "protocol"

organization := "au.id.haworth.rebuild.agent"

scalaVersion := "2.11.8"

enablePlugins(GitVersioning)

git.useGitDescribe := true

PB.protobufSettings

lazy val protocol = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "au.id.haworth.rebuild.agent.protocol"
  )

def getListOfFiles(dir: String):List[File] = {
	val d = new File(dir)
	if (d.exists && d.isDirectory) {
		d.listFiles.filter(_.isFile).toList
	} else {
		List[File]()
	}
}

PB.runProtoc in PB.protobufConfig := { args =>
  println("Compiling protocol buffer messages")

  Seq("rm", "-rf", "./golang").!!
  val newArgs: Array[String] = "-v300" +: ("--plugin=protoc-gen-go=" + sys.env("GOPATH") + "/bin/protoc-gen-go") +: "--go_out=target/" +: args.toArray

  com.github.os72.protocjar.Protoc.runProtoc(newArgs)

  println("Moving compiled golang protobuf messages from \"target/\" to \"./golang\"")
  Seq("cp", "-r", "target/github.com/RebuildTools/rebuild-agent-protocol/golang", "./").!!

  println("Committing Golang library code")
  Seq("git", "add", "./golang").!!
  Seq("git", "commit", "-m", "\"[SBT] Committing compiled protocol buffer messages for Golang\"").!

  0
}
