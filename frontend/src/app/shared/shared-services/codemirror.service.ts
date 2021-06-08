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

  private lineToPropertyBreadcrumbMap: any;
  private propertyTolineBreadcrumbMap: any;
  private _breadcrumbEditorLine = -1;
  private currentLineInEditor = 0;
  private _mergeEditor: any;

  private _content = '';  //// _content => main content inside the editor
  private _editor: CodeEditor = CodeEditor.JSON; //// setting default editor type to JSON.

  constructor() { }

  get editor(): CodeEditor {
    return this._editor;
  }
  set editor(type: CodeEditor) {
    this._editor = type;
  }

  //// This func sets the content according to the Editor Type
  mergeEditorConstruct(codemirrorTextArea: any, configuration: any, data: any): void {

    configuration.foldGutter = false;
    configuration.readOnly = true;

    //// _mergeEditor => main editor
    this._mergeEditor = codemirrorTextArea;

    //// On double click point the cursor to that area
    this._mergeEditor.on('dblclick', (instance: any, event: Event) => {
      this.breadcrumbEditorLine = instance.getCursor().line + 1;
      //SpringProfileComponent.DisplayPropertyPathOrFind = true; // circular dependency
    });

    switch (this._editor) {
      case CodeEditor.JSON: {
        this._content = JSON.stringify(data, null, 2);
        break;
      }
      case CodeEditor.YAML: {
        this._content = YAML_PRETTIER.stringify(data);
      }
    }
  }

  //// Showing the Editor
  showEditor(): void {
    this._mergeEditor.setValue(this._content);
    this._mergeEditor.setSize('100%', '430px');
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

  updateCodeMirrorVisual(profileData: ProfileDataTO[], propertyList: PropertyDetail[], jsonObject: any, codemirrorId: string): void {

    const parent = document.getElementById(codemirrorId);
    console.log(parent);
    const lineElements = parent?.getElementsByClassName('CodeMirror-linenumber CodeMirror-gutter-elt');
    console.log(lineElements);
    //const contentLineElements = parent?.getElementsByClassName('CodeMirror-line');
    //console.log(contentLineElements);
    if (lineElements) {

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
          this.yamlLineReaderInObject('', jsonObject, profileMapper, YAML_PARSER);
          break;
        }
      }
      console.log(this.propertyTolineBreadcrumbMap);
      console.log(profileMapper);

      
      const profileColorMap = new Map(profileData.map((prof, index) => [prof.profile, prof.color.color]));
      
      //tslint:disable-next-line:prefer-for-of
      
      for (let i = 0; i < propertyList.length; ++i) {
        const prop = propertyList[i];
        const lineNumber = profileMapper.get(prop.property);
        this.updateColor(lineElements[lineNumber], profileColorMap.get(prop.owner));
      }
      ///// updating color of the sied-bar
      profileData.forEach((profile, index) => {
        this.updateColor(document.getElementById(`side-bar-${index}`), profile.color.color);
      });
      /*    consistency coloring solved   . 
      if(contentLineElements)
      this.updateColor(contentLineElements[1],"#2f5d62")
      */
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
