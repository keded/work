package pro.filatov.workstation4ceb.model.fpga.parser;

import pro.filatov.workstation4ceb.model.fpga.parser.BlockCode;
import pro.filatov.workstation4ceb.model.fpga.parser.FileType;

/**
 * Created by yuri.filatov on 24.08.2016.
 */
public class FileAttr {

    private String name;
    private Integer size;
    private  Integer hexSize;
    private Integer numBits;
    private FileType fileType;
    private BlockCode blockCode;
    private Integer address;
    private boolean program;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getHexSize() {
        return hexSize;
    }

    public void setHexSize(Integer hexSize) {
        this.hexSize = hexSize;
    }

    public Integer getNumBits() {
        return numBits;
    }

    public void setNumBits(Integer numBits) {
        this.numBits = numBits;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public BlockCode getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(BlockCode blockCode) {
        this.blockCode = blockCode;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public boolean isProgram() {
        return program;
    }

    public void setProgram(boolean program) {
        this.program = program;
    }
}
