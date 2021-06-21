import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';


@Component({
  selector: 'app-warning-dialog',
  templateUrl: './warning-dialog.component.html',
  styleUrls: ['./warning-dialog.component.css']
})
export class WarningDialogComponent implements OnInit {

  warningOutput: string = "";
  constructor(private dialogRef: MatDialogRef<WarningDialogComponent>,@Inject(MAT_DIALOG_DATA) public data: any) {}

  ngOnInit(): void {
  }
  discardChanges(){
    this.warningOutput = "yes";
    this.dialogRef.close(this.warningOutput);
  }
  sendNo(){
    this.warningOutput = "no";
    this.dialogRef.close(this.warningOutput);
  }

}
