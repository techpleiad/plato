package org.techpleiad.plato.adapter.mapper;

import org.mapstruct.Mapper;
import org.techpleiad.plato.api.response.FileDetailResponseTO;
import org.techpleiad.plato.core.domain.FileDetail;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileDetailMapper {
    List<FileDetailResponseTO> convertFileDetailListToFileDetailResponseTOList(List<FileDetail> fileDetailList);

    FileDetailResponseTO convertFileDetailTOFileDetailResponseTO(FileDetail fileDetail);
}
