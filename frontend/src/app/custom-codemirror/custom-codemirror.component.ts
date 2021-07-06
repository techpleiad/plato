import { Component, Input, OnInit, AfterViewInit, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import * as CodeMirror from 'codemirror';
import { CodemirrorService } from '../shared/shared-services/codemirror.service';
import * as yaml from 'yaml';

import 'codemirror/mode/yaml/yaml';
import 'codemirror/lib/codemirror';
import 'codemirror/addon/lint/lint';
import 'codemirror/addon/lint/yaml-lint';
import 'codemirror/addon/fold/foldgutter';
import 'codemirror/addon/fold/indent-fold';
import 'codemirror/addon/fold/foldcode';

import 'codemirror/addon/edit/closebrackets';
import 'codemirror/addon/edit/matchbrackets';
import { CodeEditor } from '../shared/shared-services/codemirror.config';
import { ProfileSpecTO, PropertyDetail } from '../shared/models/ProfileSpecTO';
import { ColorProviderService } from '../shared/shared-services/color-provider.service';
import { ProfileDataTO } from '../shared/models/ProfileDataTO';
import { YamlService } from '../shared/shared-services/yaml.service';
import { SchemaTypeHandlerService } from '../shared/shared-services/schema-type-handler.service';



@Component({
  selector: 'app-custom-codemirror',
  templateUrl: './custom-codemirror.component.html',
  styleUrls: ['./custom-codemirror.component.css']
})
export class CustomCodemirrorComponent implements OnInit, AfterViewInit, OnChanges {
  @Output() modifyProfileData = new EventEmitter()

  static get Prefix(): string {
    return "codemirror-";
  }
  @Input() content: string="";
  @Input() id!: string;
  @Input() propertyList: PropertyDetail[] = [];
  @Input() ownerList: string[] = [];
  @Input() cmp: string = "";
  @Input() codemirrorMode = "YAML";
  @Input() codemirrorHeight = "400px";
  @Input() codemirrorWidth = "100%";
  @Input() isEditable = true;
  @Input() isJsonSchemaEditor = false;
  contentValid = true;
  contentChanged = false;
  additionalParams: any[] = []
  schemaPropertyClicked!: string;
  


  private codemirror: any;

  SPACES_TO_ONE_TAB = 2;
  SPACE_REPLACE = '';
  profileColorList: ProfileDataTO[]=[];

  CODEMIRROR_CONFIG: any = {
    readOnly: !this.isEditable,
    theme: 'idea',
    mode: 'yaml',
    lineNumbers: true,
    foldGutter: false,
    tabSize: this.SPACES_TO_ONE_TAB,
    indentUnit: this.SPACES_TO_ONE_TAB,
    indentWithTabs: true,
    gutters: [
      'CodeMirror-linenumbers',
      'CodeMirror-foldgutter',
      'CodeMirror-lint-markers'
    ],
    autoCloseBrackets: true,
    matchBrackets: true,
    autofocus: true
  };

  constructor(private _codemirrorService: CodemirrorService, private _colorService: ColorProviderService,
    private yamlFileService: YamlService,
    private _schemaTypeHandlerService: SchemaTypeHandlerService) {
      
    this.SPACE_REPLACE = ' '.repeat(this.SPACES_TO_ONE_TAB);
    this._codemirrorService.editor = CodeEditor.YAML;
  }

  ngOnInit(): void {
    this.yamlFileService.errorObservable$.subscribe((data:boolean)=>{
      //console.log("working")
      if(this.contentChanged===true)
        this.contentValid = !data;
      this.contentChanged = false;
    })
    
    this._schemaTypeHandlerService.includeParams$.subscribe((data:any)=>{
      console.log("_schemaTypeHandlerService running");
      this.additionalParams = data;
      if(this.schemaPropertyClicked)
      this.setAdditionalParams();
    })
    
  }
  ngAfterViewInit(): void {
    this.codemirror = CodeMirror.fromTextArea(document.getElementById(`${this.prefix}${this.id}`) as HTMLTextAreaElement,
      this.CODEMIRROR_CONFIG
      );

      this.codemirror.setSize('100%', this.codemirrorHeight);
      this.codemirror.refresh();
      if(this.codemirror){
        this._colorService.reset();
        if(this.content!=="")
        this.update();
      }
      this.codemirror.on('change',(editor: any)=>{
        this.contentChanged = true;
        
        if(editor.getValue()===""){
          this.contentValid = true;
          this.contentChanged = false;
        }
        //console.log(editor.getValue());
        let newContent = this.yamlFileService.replaceAll(editor.getValue(),'\t',this.SPACE_REPLACE);
        if(this.codemirrorMode === "YAML")
          this.yamlFileService.validateYAML(newContent);
        if(this.codemirrorMode === "JSON")
          this.yamlFileService.validateJSON(newContent);
        
          //console.log("contentChanged");
          //console.log(newContent);
        this.content = newContent;
        this.modifyProfileData.emit(newContent);
      })


      this.codemirror.on('dblclick', (instance: any, event: Event) => {
        this._schemaTypeHandlerService.resetInputValues();
        this.schemaPropertyClicked = (this._codemirrorService.lineToPropertyBreadcrumbMap.get(instance.getCursor().line+1));
        console.log(this.schemaPropertyClicked);
        if(this.isJsonSchemaEditor){
          let type = this.getSchemaPropertyType(this.schemaPropertyClicked);
          console.log(this.content);
          console.log(type);
          if(type)
            this._schemaTypeHandlerService.takeInputs(type); 
        }
      });
  }
  ngOnChanges(changes: SimpleChanges): void {
    console.log("noOnChanges");
    console.log(changes);
    
    
    if(this.content){
      console.log(this.content);
      let newContent = this.yamlFileService.replaceAll(this.content,'\t',this.SPACE_REPLACE);
        if(this.codemirrorMode === "YAML")
          this.yamlFileService.validateYAML(newContent);
        if(this.codemirrorMode === "JSON")
          this.yamlFileService.validateJSON(newContent);
    }
    //console.log(this._codemirrorService._mergeEditor.getValue());
    if(this.codemirrorMode==="JSON"){
      this._codemirrorService.editor = CodeEditor.JSON;
    }
    else if(this.codemirrorMode==="YAML"){
      this._codemirrorService.editor = CodeEditor.YAML;
    }

    this.content = this.content || "";
    this.profileColorList = [];
    this.codemirror?.refresh();
    if(this.codemirror){
      this._colorService.reset();
      if(this.content !== "")
        this.update();
      else{
        console.log(this.content);
        this._codemirrorService.content = "";
        this._codemirrorService.showEditor(this.codemirrorHeight,this.codemirrorWidth);
      }
    }
  }

  private update(): void{
    const jsonObject = yaml.parse(this.content);
    this._codemirrorService.mergeEditorConstruct(
      this.codemirror,
      JSON.parse(JSON.stringify(this.CODEMIRROR_CONFIG)),
      jsonObject,
      `${this.prefix}${this.id}-container`,
      this.cmp
    );

    this.profileColorList = [];
    this.profileColorList = this.ownerList.map((val:string)=>{
      return new ProfileDataTO(val,this._colorService.getColor());
    })

    setTimeout(() => {
      this._codemirrorService.showEditor(this.codemirrorHeight,this.codemirrorWidth);
      setTimeout(() => {
        this._codemirrorService.updateCodeMirrorVisual(this.profileColorList, this.propertyList, jsonObject,`${this.prefix}${this.id}-container`,this.cmp);
        //this.SUGGESTED_LIST = this.codemirrorService.findSuggestedPropertyList('');
      }, 200);
    }, 1000);
    
  }
  get prefix(): string {
    return CustomCodemirrorComponent.Prefix;
  }

  getSchemaPropertyType(schemaProperty: string){
    let jsonSchemaContent = JSON.parse(this.content);
    let parentList = schemaProperty.split(".");
    let curr = jsonSchemaContent;
    for(let i=0;i<parentList.length;i++){
      if(!curr[parentList[i]]){
            curr[parentList[i]] = {};
      }
      curr = curr[parentList[i]];
    }
    return curr.type;
    
  }
  setAdditionalParams(){
    console.log("setting add params");
    if(this.content){
      let jsonSchemaContent = JSON.parse(this.content);

      let parentList = this.schemaPropertyClicked.split(".");
      let curr = jsonSchemaContent;
      for(let i=0;i<parentList.length;i++){
        if(!curr[parentList[i]]){
            curr[parentList[i]] = {};
        }
        curr = curr[parentList[i]];
      }
      for(let i=0;i<this.additionalParams.length;i++){
        let param = Object.keys(this.additionalParams[i])[0];
        if(this.additionalParams[i][param]!==null && this.additionalParams[i][param]!==[]){
          curr[param] = this.additionalParams[i][param];
        }
      }
      this.content = JSON.stringify(jsonSchemaContent,null,2);
      this.update();
    
    }
    
  }

}
// constructor() -> ngOnInit() -> ngOnChanges() -> ngAfterViewInit() -> ngOnDestroy()