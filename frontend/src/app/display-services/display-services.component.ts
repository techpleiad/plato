import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import { microService } from '../microService'
import { DataManagerService } from '../shared/shared-services/data-manager.service';
import { WorkspaceDialogueComponent } from '../workspace-dialogue/workspace-dialogue.component';


@Component({
  selector: 'app-display-services',
  templateUrl: './display-services.component.html',
  styleUrls: ['./display-services.component.css']
})
export class DisplayServicesComponent implements OnInit {

  @Input() services: microService[];
  
  constructor(private _dataManagerServices: DataManagerService, public dialog: MatDialog) { 
    this.services = [];
  }
  logServices(){
    console.log(this.services);
  }
  openWorkspace(mservice: microService){
    const dialogRef = this.dialog.open(WorkspaceDialogueComponent,{
      data: mservice,
      height: '700px',
      width: '1200px',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  
  }

  ngOnInit(): void {}

}
