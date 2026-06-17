{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "sae-java2-env";

  buildInputs = with pkgs; [
    jdk17
    maven
  ];

  shellHook = ''
    echo "☕ SAE Java 2.01 — shell prêt"
    echo "   java : $(java -version 2>&1 | head -1)"
    echo "   maven: $(mvn --version 2>&1 | head -1)"
  '';
}
