import { Injectable } from '@angular/core';
import * as CodeMirror from 'codemirror';
import { CodeEditor, CodemirrorReader, JSON_PARSER, YAML_PARSER } from './codemirror.config';
import * as YAML_PRETTIER from 'yaml';
import { ProfileDataTO } from '../models/ProfileDataTO';
import { PropertyDetail } from '../models/ProfileSpecTO';

@Injectable({
  providedIn: 'root'
})
export class CodemirrorService {

  public lineToPropertyBreadcrumbMap: any;
  private propertyTolineBreadcrumbMap = new Map();
  private _breadcrumbEditorLine = -1;
  private currentLineInEditor = 0;
  private _mergeEditor: any;

  private _content = '';  //// _content => main content inside the editor
  private _jsonContent: any;
  private _editor: CodeEditor = CodeEditor.JSON; //// setting default editor type to JSON.

  private _profileData: ProfileDataTO[] = [];
  private _propertyList: any[] = [];
  private _profileMapper: any = null;
  private _lineToDivMapper = new Map();

  private missingLineNumber: any;

  constructor() { }

  get editor(): CodeEditor {
    return this._editor;
  }
  set editor(type: CodeEditor) {
    this._editor = type;
  }

  //// This func sets the content according to the Editor Type
  mergeEditorConstruct(codemirrorTextArea: any, configuration: any, data: any, codemirrorId: any, cmp: string): void {

    configuration.foldGutter = false;
    configuration.readOnly = true;

    //// _mergeEditor => main editor
    this._mergeEditor = codemirrorTextArea;
    this._mergeEditor.refresh();
    //// On double click point the cursor to that area
    
    
    this._mergeEditor.on('update', (instance: any) => {
      this.onScrollCodemirrorUpdate(codemirrorId,cmp);
    });
    

    switch (this._editor) {
      case CodeEditor.JSON: {
        this._content = JSON.stringify(data, null, 2);
        break;
      }
      case CodeEditor.YAML: {
        console.log(data);
        //YAML_PRETTIER.scalarOptions.str.defaultType  = 'PLAIN';
        this._content = YAML_PRETTIER.stringify(data);
        console.log(this._content);
      }
    }
  }

  //// Showing the Editor
  showEditor(codemirrorHeight: string, codemirrorWidth: string): void {
    this._mergeEditor.setValue(this._content);
    this._mergeEditor.setSize(codemirrorWidth, codemirrorHeight);

    this._mergeEditor.refresh();
  }

  set content(data: string) {
    this._content = data;
  }
  get content(): string {
    return this._content;
  }

  set breadcrumbEditorLine(line: number) {
    this._breadcrumbEditorLine = line;
  }

  updateCodeMirrorVisual(profileData: ProfileDataTO[], propertyList: PropertyDetail[], jsonObject: any, codemirrorId: string,cmp: string): void {
    
    this._mergeEditor.refresh();
    this._lineToDivMapper = new Map();

    this._propertyList = propertyList;
    this._profileData = profileData;

    const profileMapper = new Map();

    this.lineToPropertyBreadcrumbMap = new Map();
    this.propertyTolineBreadcrumbMap = new Map();
    this._breadcrumbEditorLine = -1;

    switch (this._editor) {
      case CodeEditor.JSON: {
        this.currentLineInEditor = JSON_PARSER.INITIAL_LINE;
        this.jsonLineReader('', jsonObject, profileMapper, JSON_PARSER);
        break;
      }
      case CodeEditor.YAML: {
        this.currentLineInEditor = YAML_PARSER.INITIAL_LINE;
        if(jsonObject!==null)
        this.yamlLineReaderInObject('', jsonObject, profileMapper, YAML_PARSER);
        break;
      }
    }
    this._profileMapper = profileMapper;
    this.onScrollCodemirrorUpdate(codemirrorId,cmp);
    if(this.missingLineNumber){
      let y = Number(`${this.missingLineNumber}`);
      console.log("property added at line ", y);
      this._mergeEditor.focus();
      this._mergeEditor.setCursor({line: y-1, ch: 0});
      //this.missingLineNumber = undefined;
    }
  }

  private onScrollCodemirrorUpdate(codemirrorId: string,cmp: string): void {
    // profileMapper => property to line number
    //get current lines property and map to the div
    //then for each property get the line number
    //
    const parent = document.getElementById(codemirrorId);
    const lineElements = parent?.getElementsByClassName('CodeMirror-linenumber CodeMirror-gutter-elt');
    if (lineElements) {
      for (let i = 0; i < lineElements.length; ++i) {
        const div: any = lineElements[i];
        this._lineToDivMapper.set(div['innerText'], lineElements[i]);
      }
      const profileColorMap = new Map(this._profileData.map((prof, index) => [prof.profile, prof.color.color]));
      if(this._propertyList.length === 0){
        for (let i = 0; i < lineElements.length; ++i) {
          this.updateColor(lineElements[i],"#f7f7f7");
        }
      }
      this._propertyList.forEach(prop => {
        const lineNumber = this._profileMapper.get(prop.property);
        this.updateColor(this._lineToDivMapper.get(`${lineNumber}`), profileColorMap.get(prop.owner));
      });
      this._profileData.forEach((profile, index) => {
        this.updateColor(document.getElementById(`side-bar-${index}`), profile.color.color);
      });
    }
    //missing property -> actual line number -> 
    let missingProp = cmp;
    this.missingLineNumber = this.propertyTolineBreadcrumbMap.get(missingProp);
    //console.log(this.missingLineNumber);
    
    if(this.missingLineNumber){
      this.updateColor(this._lineToDivMapper.get(`${this.missingLineNumber}`), '#78DEC7');
    }
    
    
  }

  updateColor(element: any, color: any): void {
    if (element) {
      element.style['background-color'] = color;
    }
  }

  propertyType(value: any): string {
    if (value instanceof Array) {
      return 'Array';
    }
    else if (value instanceof Object) {
      return 'object';
    }
    return 'primitive';
  }

  jsonLineReader(path: string, root: any,  profileMapper: any, config: CodemirrorReader, isArray: boolean = false): void {
    const parentIndex = this.currentLineInEditor;
    for (const pro of Object.keys(root)) {
      const val = root[pro];
      const newPath = this.generatePropertyPath(path, pro);

      if (this.propertyType(val) === 'primitive' && isArray) {
        profileMapper.set(path, parentIndex - 1);
        this.lineToPropertyBreadcrumbMap.set(this.currentLineInEditor, `${newPath}.${val}`);
        this.propertyTolineBreadcrumbMap.set(`${newPath}.${val}`, this.currentLineInEditor);
        this.currentLineInEditor += config.ARRAY_PRIMITIVE_PROPERTY;
        continue;
      }

      this.lineToPropertyBreadcrumbMap.set(this.currentLineInEditor, newPath);
      this.propertyTolineBreadcrumbMap.set(newPath, this.currentLineInEditor);

      if (val instanceof Object) {
        this.currentLineInEditor += config.NEXT_PROPERTY;
        this.jsonLineReader(newPath, val, profileMapper, config, val instanceof Array);
      }
      else {
        profileMapper.set(newPath, this.currentLineInEditor);
      }
      this.currentLineInEditor += config.NEXT_OBJECT_PROPERTY;
    }
  }

  generatePropertyPath(path: string, property: string): string {
    if (path === '') {
      return property;
    }
    return `${path}.${property}`;
  }

  getEditorBreadcrumbArray(): string[] {
    return this.lineToPropertyBreadcrumbMap?.get(this._breadcrumbEditorLine)?.split('.') || [''];
  }

  updateEditorCursorPosition(index: number): void {
    this._mergeEditor.focus();

    if (index === this.getEditorBreadcrumbArray().length) {
      return;
    }
    try {
      this.highlightPropertyInCursorLine(this.propertyTolineBreadcrumbMap
        .get(this.getEditorBreadcrumbArray().slice(0, index + 1).join('.')));
    }
    catch (exception) { // can case when primitive index
      console.error(exception);
      this.updateEditorCursorPosition(index + 1);
    }
  }

  private highlightPropertyInCursorLine(cursorPos: number): void {
    if (cursorPos !== null) {
      this._mergeEditor.setCursor(cursorPos - 1, 0);
    }

    const startLastIndex = this.getPropertyStartEndIndex(this._mergeEditor.getLine(cursorPos - 1));
    this._mergeEditor.setSelection(
      {line: cursorPos - 1, ch: startLastIndex[0]},
      {line: cursorPos - 1, ch: startLastIndex[1]}
    );
  }

  highlightPropertyInPropertyPath(path: string): void {
    try {
      this.highlightPropertyInCursorLine(this.propertyTolineBreadcrumbMap.get(path));
    }
    catch (e) {}
  }

  getPropertyStartEndIndex(line: string): any {
    switch (this._editor) {
      ///// JSON PropertyFind Strategy
      case CodeEditor.JSON: {
        const start = line.indexOf('"');
        if (start === -1) {
          return [0, 0];
        }
        for (let last = start + 1; last < line.length; ++last) {
          if (line.charAt(last - 1) !== '\\' && line.charAt(last) === '"') {
            return [start, last + 1];
          }
        }
        break;
      }
      //// YAML PropertyFind Strategy
      case CodeEditor.YAML: {
        const startYAML = this.findFirstAlphaIndex(line);
        if (startYAML === -1) {
          return [0, 0];
        }
        return [startYAML, line.indexOf(':')];
      }
    }
    return [0, 0];
  }

  findFirstAlphaIndex(line: string): number {
    function isLetter(c: any): boolean {
      return c.toLowerCase() !== c.toUpperCase();
    }
    for (let index = 0; index < line.length; index++) {
      if (isLetter(line.charAt(index))) {
        return index;
      }
    }
    return -1;
  }

  findSuggestedPropertyList(text: string): string[] {
    // console.log('search : ', text.substr(text.lastIndexOf('.') + 1));
    const suggestedPropertyList: string[] = [];
    this.propertyTolineBreadcrumbMap.forEach((value: number, key: string) => {
      if (key.startsWith(text) && key.length !== text.length) {
        suggestedPropertyList.push(key);
      }
    });
    return suggestedPropertyList;
  }

  addYAMLMultiStringNextLines(val: any): number {
    if (typeof val === 'string') {
      return val.split('\n').length;
    }
    return 0;
  }

  yamlLineReaderInObject(path: string, root: any,  profileMapper: any, config: CodemirrorReader): void {
    for (const pro of Object.keys(root)) {

      const val = root[pro];
      const newPath = this.generatePropertyPath(path, pro);

      this.lineToPropertyBreadcrumbMap.set(this.currentLineInEditor, newPath);
      this.propertyTolineBreadcrumbMap.set(newPath, this.currentLineInEditor);

      profileMapper.set(newPath, this.currentLineInEditor);
      this.currentLineInEditor += (Math.max(1, this.addYAMLMultiStringNextLines(val)));
      if (val instanceof Array) {
        this.yamlLineReaderInArray(newPath, val, profileMapper, config);
      }
      else if (val instanceof Object) {
        this.yamlLineReaderInObject(newPath, val, profileMapper, config);
      }
    }
  }

  yamlLineReaderInArray(path: string, root: any,  profileMapper: any, config: CodemirrorReader): void {

    //const parentIndex = this.currentLineInEditor;
    for (const pro of Object.keys(root)) {
      const val = root[pro];
      const newPath = this.generatePropertyPath(path, pro);

      //profileMapper.set(path, parentIndex);
      switch (this.propertyType(val)) {
        case 'primitive': {
          this.lineToPropertyBreadcrumbMap.set(this.currentLineInEditor, `${newPath}.${val}`);
          this.propertyTolineBreadcrumbMap.set(`${newPath}.${val}`, this.currentLineInEditor);
          this.currentLineInEditor += this.addYAMLMultiStringNextLines(val);
          break;
        }
        case 'Array': {
          this.yamlLineReaderInArray(newPath, val, profileMapper, config);
          break;
        }
        default: {
          this.yamlLineReaderInObject(newPath, val, profileMapper, config);
        }
      }
    }
  }
}
