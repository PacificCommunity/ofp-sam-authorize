/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
module authorize.core {
    exports org.spc.ofp.project.authorize.io;
    exports org.spc.ofp.project.authorize.signature;
    exports org.spc.ofp.project.authorize.signature.jsign;
    exports org.spc.ofp.project.authorize.signature.jarsigner;
    requires java.logging;
    requires jsign;
}
