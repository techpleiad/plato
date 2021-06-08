export const JSON_PARSER: CodemirrorReader = {
    ARRAY_PRIMITIVE_PROPERTY: 1,
    NEXT_PROPERTY: 1,
    NEXT_OBJECT_PROPERTY: 1,
    OBJECT_CLOSE_NEXT_LINE: 1,
    INITIAL_LINE: 2,
    ARRAY_IN_NEXT_LINE: 1
  };
  
  export const YAML_PARSER: CodemirrorReader = {
    ARRAY_PRIMITIVE_PROPERTY: 1,
    NEXT_PROPERTY: 1,
    NEXT_OBJECT_PROPERTY: 1,
    OBJECT_CLOSE_NEXT_LINE: 0,
    INITIAL_LINE: 1,
    ARRAY_IN_NEXT_LINE: 0
  };
  
  export interface CodemirrorReader {
    ARRAY_PRIMITIVE_PROPERTY: number;
    NEXT_PROPERTY: number;
    NEXT_OBJECT_PROPERTY: number;
    OBJECT_CLOSE_NEXT_LINE: number;
    INITIAL_LINE: number;
    ARRAY_IN_NEXT_LINE: number;
  }
  
  export enum CodeEditor {
    JSON = 'JSON',
    YAML = 'YAML'
  }
  